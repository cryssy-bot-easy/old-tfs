package com.ucpb.tfs.application.service;

import com.ucpb.tfs.domain.product.*;
import com.ucpb.tfs.domain.product.enums.ExportBillType;
import com.ucpb.tfs.domain.product.enums.IndemnityType;
import com.ucpb.tfs.domain.product.enums.ProductType;
import com.ucpb.tfs.domain.product.enums.TradeProductStatus;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.*;
import com.ucpb.tfs.report.dw.regen.*;
import com.ucpb.tfs.report.dw.regen.dao.*;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.SqlTimestampConverter;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: IPCVal
 */
@Component
public class AuditRegenService {

    @Autowired
    private TradeServiceRepository tradeServiceRepository;

    @Autowired
    private RevInfoDao revInfoDao;

    @Autowired
    private TradeProductAuditDao tradeProductAuditDao;

    @Autowired
    private DocumentAgainstAcceptanceAuditDao documentAgainstAcceptanceAuditDao;

    @Autowired
    private DocumentAgainstPaymentAuditDao documentAgainstPaymentAuditDao;

    @Autowired
    private OpenAccountAuditDao openAccountAuditDao;

    @Autowired
    private ExportBillsAuditDao exportBillsAuditDao;

    @Autowired
    private IndemnityAuditDao indemnityAuditDao;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, readOnly = false)
    public void regenerateMigrated() throws Exception {

        List<TradeService> tradeServices = tradeServiceRepository.getAllOriginalTradeServiceMigratedData();

        System.out.println("tradeServices.size() = " + tradeServices.size() + "\n");

        BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
        beanUtilsBean.getConvertUtils().register(new DateConverter(null), Date.class);
        beanUtilsBean.getConvertUtils().register(new BigDecimalConverter(null), BigDecimal.class);
        beanUtilsBean.getConvertUtils().register(new SqlTimestampConverter(null), Timestamp.class);

        mainLoop:
        for (TradeService tradeService : tradeServices) {

            DocumentNumber documentNumber = tradeService.getDocumentNumber();
            ServiceType serviceType = tradeService.getServiceType();
            DocumentClass documentClass = tradeService.getDocumentClass();
            DocumentType documentType = tradeService.getDocumentType();
            DocumentSubType1 documentSubType1 = tradeService.getDocumentSubType1();
            DocumentSubType2 documentSubType2 = tradeService.getDocumentSubType2();

            if (documentClass != null) {

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTime(tradeService.getProcessDate());
                long revtstmp = calendar.getTimeInMillis();

                // System.out.println("tradeServiceId = " + tradeService.getTradeServiceId().toString());
                // System.out.println("tradeService.getProcessDate().getTime() = " + tradeService.getProcessDate().getTime());
                // System.out.println("calendar.getTimeZone().toString() = " + calendar.getTimeZone().toString());
                // System.out.println("revtstmp = " + revtstmp);

                RevInfo revInfo = new RevInfo();
                revInfo.setRevtstmp(revtstmp);

                int val = revInfoDao.insert(revInfo);
                int maxRev = revInfoDao.selectMaxRev();

                System.out.println("----------------");
                System.out.println("MAXREV           = " + maxRev);
                System.out.println("documentNumber   = " + documentNumber);
                System.out.println("serviceType      = " + serviceType);
                System.out.println("documentClass    = " + documentClass);
                System.out.println("documentType     = " + documentType);
                System.out.println("documentSubType1 = " + documentSubType1);
                System.out.println("documentSubType2 = " + documentSubType2);
                System.out.println("----------------\n");

                // Non-LC
                if (documentClass.equals(DocumentClass.DA)) {

                    System.out.println("DA");

                    if (serviceType.equals(ServiceType.SETTLEMENT)) {

                        DocumentAgainstAcceptance da = new DocumentAgainstAcceptance(tradeService.getDocumentNumber(), tradeService.getDetails());
                        String lastTransactionSettlement = buildLastLcTransactionString(ServiceType.SETTLEMENT, documentClass, documentType);
                        da.updateLastTransaction(lastTransactionSettlement);

                        da.settle(tradeService.getDetails());

                        TradeProduct daTradeProduct = (TradeProduct)da;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        DocumentAgainstAcceptanceAudit documentAgainstAcceptanceAudit = new DocumentAgainstAcceptanceAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, daTradeProduct);
                        beanUtilsBean.copyProperties(documentAgainstAcceptanceAudit, da);

                        if (tradeService.getDetails().get("outstandingAmount") != null && tradeService.getDetails().get("productAmount") != null) {
                            String outstandingAmountStr = (String)tradeService.getDetails().get("outstandingAmount");
                            BigDecimal outstandingAmount = new BigDecimal(outstandingAmountStr.replaceAll(",",""));
                            String productAmountStr = (String)tradeService.getDetails().get("productAmount");
                            BigDecimal productAmount = new BigDecimal(productAmountStr.replaceAll(",",""));
                            BigDecimal diff = outstandingAmount.subtract(productAmount);
                            documentAgainstAcceptanceAudit.setOutstandingAmount(diff);
                            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                                tradeProductAudit.setStatus(TradeProductStatus.ACCEPTED);
                            } else {
                                tradeProductAudit.setStatus(TradeProductStatus.CLOSED);
                            }
                        } else {
                            tradeProductAudit.setStatus(TradeProductStatus.ACCEPTED);
                        }

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("1"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.DA);
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        documentAgainstAcceptanceAudit.setRevId(maxRev);
                        documentAgainstAcceptanceAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        if(((String)tradeService.getDetails().get("documentType")).equals("FOREIGN")) {
                            documentAgainstAcceptanceAudit.setDocumentType(DocumentType.FOREIGN);
                        } else if(((String)tradeService.getDetails().get("documentType")).equals("DOMESTIC")) {
                            documentAgainstAcceptanceAudit.setDocumentType(DocumentType.DOMESTIC);
                        }
                        documentAgainstAcceptanceAudit.setTsNumber((String)tradeService.getDetails().get("tsNumber"));

                        documentAgainstAcceptanceAudit.setSettledDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("documentAgainstAcceptanceAudit.getDocumentNumber() = " + documentAgainstAcceptanceAudit.getDocumentNumber());
                        System.out.println("documentAgainstAcceptanceAudit.getSettledDate() = " + documentAgainstAcceptanceAudit.getSettledDate());
                        System.out.println("tradeProductAudit.getAmount() = " + tradeProductAudit.getAmount().toPlainString());
                        System.out.println("documentAgainstAcceptanceAudit.getOutstandingAmount() = " + documentAgainstAcceptanceAudit.getOutstandingAmount().toPlainString());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        documentAgainstAcceptanceAuditDao.insert(documentAgainstAcceptanceAudit);

                    } else if (serviceType.equals(ServiceType.CANCELLATION)) {

                        DocumentAgainstAcceptance da = new DocumentAgainstAcceptance(tradeService.getDocumentNumber(), tradeService.getDetails());
                        String lastTransactionSettlement = buildLastLcTransactionString(ServiceType.CANCELLATION, documentClass, documentType);
                        da.updateLastTransaction(lastTransactionSettlement);

                        da.cancelDa();

                        TradeProduct daTradeProduct = (TradeProduct)da;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        DocumentAgainstAcceptanceAudit documentAgainstAcceptanceAudit = new DocumentAgainstAcceptanceAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, daTradeProduct);
                        beanUtilsBean.copyProperties(documentAgainstAcceptanceAudit, da);

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("1"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.DA);
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                        tradeProductAudit.setStatus(daTradeProduct.getStatus());

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        documentAgainstAcceptanceAudit.setRevId(maxRev);
                        documentAgainstAcceptanceAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        if(((String)tradeService.getDetails().get("documentType")).equals("FOREIGN")) {
                            documentAgainstAcceptanceAudit.setDocumentType(DocumentType.FOREIGN);
                        } else if(((String)tradeService.getDetails().get("documentType")).equals("DOMESTIC")) {
                            documentAgainstAcceptanceAudit.setDocumentType(DocumentType.DOMESTIC);
                        }
                        documentAgainstAcceptanceAudit.setTsNumber((String)tradeService.getDetails().get("tsNumber"));

                        documentAgainstAcceptanceAudit.setCancelledDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("documentAgainstAcceptanceAudit.getDocumentNumber() = " + documentAgainstAcceptanceAudit.getDocumentNumber());
                        System.out.println("documentAgainstAcceptanceAudit.getCancelledDate() = " + documentAgainstAcceptanceAudit.getCancelledDate());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        documentAgainstAcceptanceAuditDao.insert(documentAgainstAcceptanceAudit);
                    }

                } else if (documentClass.equals(DocumentClass.DP)) {

                    System.out.println("DP");

                    if (serviceType.equals(ServiceType.SETTLEMENT)) {

                        DocumentAgainstPayment dp = new DocumentAgainstPayment(tradeService.getDocumentNumber(), tradeService.getDetails());
                        String lastTransactionSettlement = buildLastLcTransactionString(ServiceType.SETTLEMENT, documentClass, documentType);
                        dp.updateLastTransaction(lastTransactionSettlement);

                        dp.settle(tradeService.getDetails());

                        TradeProduct dpTradeProduct = (TradeProduct)dp;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        DocumentAgainstPaymentAudit documentAgainstPaymentAudit = new DocumentAgainstPaymentAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, dpTradeProduct);
                        beanUtilsBean.copyProperties(documentAgainstPaymentAudit, dp);

                        if (tradeService.getDetails().get("outstandingAmount") != null && tradeService.getDetails().get("productAmount") != null) {
                            String outstandingAmountStr = (String)tradeService.getDetails().get("outstandingAmount");
                            BigDecimal outstandingAmount = new BigDecimal(outstandingAmountStr.replaceAll(",",""));
                            String productAmountStr = (String)tradeService.getDetails().get("productAmount");
                            BigDecimal productAmount = new BigDecimal(productAmountStr.replaceAll(",",""));
                            BigDecimal diff = outstandingAmount.subtract(productAmount);
                            documentAgainstPaymentAudit.setOutstandingAmount(diff);
                            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                                tradeProductAudit.setStatus(TradeProductStatus.NEGOTIATED);
                            } else {
                                tradeProductAudit.setStatus(TradeProductStatus.CLOSED);
                            }
                        } else {
                            tradeProductAudit.setStatus(TradeProductStatus.ACCEPTED);
                        }

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("1"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.DP);
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        documentAgainstPaymentAudit.setRevId(maxRev);
                        documentAgainstPaymentAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        if(((String)tradeService.getDetails().get("documentType")).equals("FOREIGN")) {
                            documentAgainstPaymentAudit.setDocumentType(DocumentType.FOREIGN);
                        } else if(((String)tradeService.getDetails().get("documentType")).equals("DOMESTIC")) {
                            documentAgainstPaymentAudit.setDocumentType(DocumentType.DOMESTIC);
                        }
                        documentAgainstPaymentAudit.setTsNumber((String)tradeService.getDetails().get("tsNumber"));

                        documentAgainstPaymentAudit.setSettledDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("documentAgainstPaymentAudit.getDocumentNumber() = " + documentAgainstPaymentAudit.getDocumentNumber());
                        System.out.println("documentAgainstPaymentAudit.getSettledDate() = " + documentAgainstPaymentAudit.getSettledDate());
                        System.out.println("tradeProductAudit.getAmount() = " + tradeProductAudit.getAmount().toPlainString());
                        System.out.println("documentAgainstPaymentAudit.getOutstandingAmount() = " + documentAgainstPaymentAudit.getOutstandingAmount().toPlainString());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        documentAgainstPaymentAuditDao.insert(documentAgainstPaymentAudit);

                    } else if (serviceType.equals(ServiceType.CANCELLATION)) {

                        DocumentAgainstPayment dp = new DocumentAgainstPayment(tradeService.getDocumentNumber(), tradeService.getDetails());
                        String lastTransactionSettlement = buildLastLcTransactionString(ServiceType.CANCELLATION, documentClass, documentType);
                        dp.updateLastTransaction(lastTransactionSettlement);

                        dp.cancelDp();

                        TradeProduct daTradeProduct = (TradeProduct)dp;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        DocumentAgainstPaymentAudit documentAgainstPaymentAudit = new DocumentAgainstPaymentAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, daTradeProduct);
                        beanUtilsBean.copyProperties(documentAgainstPaymentAudit, dp);

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("1"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.DP);
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                        tradeProductAudit.setStatus(daTradeProduct.getStatus());

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        documentAgainstPaymentAudit.setRevId(maxRev);
                        documentAgainstPaymentAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        if(((String)tradeService.getDetails().get("documentType")).equals("FOREIGN")) {
                            documentAgainstPaymentAudit.setDocumentType(DocumentType.FOREIGN);
                        } else if(((String)tradeService.getDetails().get("documentType")).equals("DOMESTIC")) {
                            documentAgainstPaymentAudit.setDocumentType(DocumentType.DOMESTIC);
                        }
                        documentAgainstPaymentAudit.setTsNumber((String)tradeService.getDetails().get("tsNumber"));

                        documentAgainstPaymentAudit.setCancelledDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("documentAgainstPaymentAudit.getDocumentNumber() = " + documentAgainstPaymentAudit.getDocumentNumber());
                        System.out.println("documentAgainstPaymentAudit.getCancelledDate() = " + documentAgainstPaymentAudit.getCancelledDate());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        documentAgainstPaymentAuditDao.insert(documentAgainstPaymentAudit);
                    }

                } else if (documentClass.equals(DocumentClass.OA)) {

                    System.out.println("OA");

                    if (serviceType.equals(ServiceType.SETTLEMENT)) {

                        OpenAccount oa = new OpenAccount(tradeService.getDocumentNumber(), tradeService.getDetails());
                        String lastTransactionSettlement = buildLastLcTransactionString(ServiceType.SETTLEMENT, documentClass, documentType);
                        oa.updateLastTransaction(lastTransactionSettlement);

                        oa.settle(tradeService.getDetails());

                        TradeProduct oaTradeProduct = (TradeProduct)oa;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        OpenAccountAudit openAccountAudit = new OpenAccountAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, oaTradeProduct);
                        beanUtilsBean.copyProperties(openAccountAudit, oa);

                        if (tradeService.getDetails().get("outstandingAmount") != null && tradeService.getDetails().get("productAmount") != null) {
                            String outstandingAmountStr = (String)tradeService.getDetails().get("outstandingAmount");
                            BigDecimal outstandingAmount = new BigDecimal(outstandingAmountStr.replaceAll(",",""));
                            String productAmountStr = (String)tradeService.getDetails().get("productAmount");
                            BigDecimal productAmount = new BigDecimal(productAmountStr.replaceAll(",",""));
                            BigDecimal diff = outstandingAmount.subtract(productAmount);
                            openAccountAudit.setOutstandingAmount(diff);
                            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                                tradeProductAudit.setStatus(TradeProductStatus.NEGOTIATED);
                            } else {
                                tradeProductAudit.setStatus(TradeProductStatus.CLOSED);
                            }
                        } else {
                            tradeProductAudit.setStatus(TradeProductStatus.ACCEPTED);
                        }

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("1"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.OA);
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        openAccountAudit.setRevId(maxRev);
                        openAccountAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        if(((String)tradeService.getDetails().get("documentType")).equals("FOREIGN")) {
                            openAccountAudit.setDocumentType(DocumentType.FOREIGN);
                        } else if(((String)tradeService.getDetails().get("documentType")).equals("DOMESTIC")) {
                            openAccountAudit.setDocumentType(DocumentType.DOMESTIC);
                        }
                        openAccountAudit.setTsNumber((String)tradeService.getDetails().get("tsNumber"));

                        openAccountAudit.setSettledDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("openAccountAudit.getDocumentNumber() = " + openAccountAudit.getDocumentNumber());
                        System.out.println("openAccountAudit.getSettledDate() = " + openAccountAudit.getSettledDate());
                        System.out.println("tradeProductAudit.getAmount() = " + tradeProductAudit.getAmount().toPlainString());
                        System.out.println("openAccountAudit.getOutstandingAmount() = " + openAccountAudit.getOutstandingAmount().toPlainString());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        openAccountAuditDao.insert(openAccountAudit);

                    } else if (serviceType.equals(ServiceType.CANCELLATION)) {

                        OpenAccount oa = new OpenAccount(tradeService.getDocumentNumber(), tradeService.getDetails());
                        String lastTransactionSettlement = buildLastLcTransactionString(ServiceType.CANCELLATION, documentClass, documentType);
                        oa.updateLastTransaction(lastTransactionSettlement);

                        oa.cancelOa();

                        TradeProduct oaTradeProduct = (TradeProduct)oa;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        OpenAccountAudit openAccountAudit = new OpenAccountAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, oaTradeProduct);
                        beanUtilsBean.copyProperties(openAccountAudit, oa);

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("1"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.OA);
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                        tradeProductAudit.setStatus(oaTradeProduct.getStatus());

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        openAccountAudit.setRevId(maxRev);
                        openAccountAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        if(((String)tradeService.getDetails().get("documentType")).equals("FOREIGN")) {
                            openAccountAudit.setDocumentType(DocumentType.FOREIGN);
                        } else if(((String)tradeService.getDetails().get("documentType")).equals("DOMESTIC")) {
                            openAccountAudit.setDocumentType(DocumentType.DOMESTIC);
                        }
                        openAccountAudit.setTsNumber((String)tradeService.getDetails().get("tsNumber"));

                        openAccountAudit.setCancelledDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("openAccountAudit.getDocumentNumber() = " + openAccountAudit.getDocumentNumber());
                        System.out.println("openAccountAudit.getCancelledDate() = " + openAccountAudit.getCancelledDate());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        openAccountAuditDao.insert(openAccountAudit);
                    }

                // Export Bills for Collection
                } else if (documentClass.equals(DocumentClass.BC)) {

                    System.out.println("Export Bills");

                    // Only FOREIGN for migrated data
                    if (documentType.equals(DocumentType.FOREIGN)) {

                        if (serviceType.equals(ServiceType.SETTLEMENT)) {

                            ExportBills exportBills = new ExportBills(tradeService.getDocumentNumber(), tradeService.getDetails(), ProductType.BC, ExportBillType.EBC);

                            exportBills.setLoanDetails(tradeService.getDetails());

                            if (tradeService.getDetails().get("paymentMode") != null && "LC".equals((String) tradeService.getDetails().get("paymentMode"))) {
                                exportBills.setLcDetails(tradeService.getDetails());
                            } else if (tradeService.getDetails().get("paymentMode") != null &&
                                    ("DA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DP".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "OA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DR".equals((String) tradeService.getDetails().get("paymentMode")))) {
                                exportBills.setNonLcDetails(tradeService.getDetails());
                            }

                            BigDecimal totalAmountClaimedAmount = BigDecimal.ZERO;
                            Date totalAmountClaimedDate = null;
                            Currency totalAmountClaimedCurrency = null;

                            if ("A".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yyyy");

                                if (tradeService.getDetails().get("totalAmountClaimedDate") != null) {
                                    totalAmountClaimedDate = simpleDateFormat.parse((String) tradeService.getDetails().get("totalAmountClaimedDate"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedA") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedA"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyA") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyA"));
                                }

                            } else if ("B".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                if (tradeService.getDetails().get("totalAmountClaimedB") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedB"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyB") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyB"));
                                }
                            }

                            exportBills.setTotalAmountDetails(totalAmountClaimedDate, totalAmountClaimedAmount, totalAmountClaimedCurrency);

                            exportBills.settleExportBills(new BigDecimal((String) tradeService.getDetails().get("proceedsAmount")),
                                                          (String) tradeService.getDetails().get("partialNego"));

                            exportBills.updateDetails(tradeService.getDetails());

                            exportBills.updateStatus(TradeProductStatus.SETTLED);

                            TradeProduct exportBillsTradeProduct = (TradeProduct)exportBills;
                            TradeProductAudit tradeProductAudit = new TradeProductAudit();
                            ExportBillsAudit exportBillsAudit = new ExportBillsAudit();

                            beanUtilsBean.copyProperties(tradeProductAudit, exportBillsTradeProduct);
                            beanUtilsBean.copyProperties(exportBillsAudit, exportBills);

                            tradeProductAudit.setRevId(maxRev);
                            tradeProductAudit.setRevType(Short.valueOf("1"));
                            tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                            tradeProductAudit.setProductType(ProductType.EXPORT_BILLS);
                            tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                            tradeProductAudit.setStatus(exportBillsTradeProduct.getStatus());

                            tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                            tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                            tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                            tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                            exportBillsAudit.setRevId(maxRev);
                            exportBillsAudit.setDocumentNumber(tradeService.getDocumentNumber());
                            exportBillsAudit.setSettlementDate(tradeService.getProcessDate());
                            exportBillsAudit.setAccountType(exportBills.getAccountType());
                            exportBillsAudit.setNegotiationDate(exportBills.getNegotiationDate());

                            System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                            System.out.println("exportBillsAudit.getDocumentNumber() = " + exportBillsAudit.getDocumentNumber());
                            System.out.println("exportBillsAudit.getNegotiationDate() = " + exportBillsAudit.getNegotiationDate());
                            System.out.println("exportBillsAudit.getSettledDate() = " + exportBillsAudit.getSettlementDate());

                            tradeProductAuditDao.insert(tradeProductAudit);
                            exportBillsAuditDao.insert(exportBillsAudit);

                        } else if (serviceType.equals(ServiceType.CANCELLATION)) {

                            ExportBills exportBills = new ExportBills(tradeService.getDocumentNumber(), tradeService.getDetails(), ProductType.BC, ExportBillType.EBC);

                            exportBills.setLoanDetails(tradeService.getDetails());

                            if (tradeService.getDetails().get("paymentMode") != null && "LC".equals((String) tradeService.getDetails().get("paymentMode"))) {
                                exportBills.setLcDetails(tradeService.getDetails());
                            } else if (tradeService.getDetails().get("paymentMode") != null &&
                                    ("DA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DP".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "OA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DR".equals((String) tradeService.getDetails().get("paymentMode")))) {
                                exportBills.setNonLcDetails(tradeService.getDetails());
                            }

                            BigDecimal totalAmountClaimedAmount = BigDecimal.ZERO;
                            Date totalAmountClaimedDate = null;
                            Currency totalAmountClaimedCurrency = null;

                            if ("A".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yyyy");

                                if (tradeService.getDetails().get("totalAmountClaimedDate") != null) {
                                    totalAmountClaimedDate = simpleDateFormat.parse((String) tradeService.getDetails().get("totalAmountClaimedDate"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedA") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedA"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyA") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyA"));
                                }

                            } else if ("B".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                if (tradeService.getDetails().get("totalAmountClaimedB") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedB"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyB") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyB"));
                                }
                            }

                            exportBills.setTotalAmountDetails(totalAmountClaimedDate, totalAmountClaimedAmount, totalAmountClaimedCurrency);

                            exportBills.cancelExportBills((String) tradeService.getDetails().get("reasonForCancellation"));

                            exportBills.updateDetails(tradeService.getDetails());

                            TradeProduct exportBillsTradeProduct = (TradeProduct)exportBills;
                            TradeProductAudit tradeProductAudit = new TradeProductAudit();
                            ExportBillsAudit exportBillsAudit = new ExportBillsAudit();

                            beanUtilsBean.copyProperties(tradeProductAudit, exportBillsTradeProduct);
                            beanUtilsBean.copyProperties(exportBillsAudit, exportBills);

                            tradeProductAudit.setRevId(maxRev);
                            tradeProductAudit.setRevType(Short.valueOf("1"));
                            tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                            tradeProductAudit.setProductType(ProductType.EXPORT_BILLS);
                            tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                            tradeProductAudit.setStatus(exportBillsTradeProduct.getStatus());

                            tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                            tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                            tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                            tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                            exportBillsAudit.setRevId(maxRev);
                            exportBillsAudit.setDocumentNumber(tradeService.getDocumentNumber());
                            exportBillsAudit.setSettlementDate(tradeService.getProcessDate());
                            exportBillsAudit.setAccountType(exportBills.getAccountType());
                            exportBillsAudit.setNegotiationDate(exportBills.getNegotiationDate());

                            System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                            System.out.println("exportBillsAudit.getDocumentNumber() = " + exportBillsAudit.getDocumentNumber());
                            System.out.println("exportBillsAudit.getNegotiationDate() = " + exportBillsAudit.getNegotiationDate());
                            System.out.println("exportBillsAudit cancelled date = " + exportBillsAudit.getSettlementDate());

                            tradeProductAuditDao.insert(tradeProductAudit);
                            exportBillsAuditDao.insert(exportBillsAudit);
                        }
                    }

                // Indemnity
                } else if (documentClass.equals(DocumentClass.INDEMNITY)) {

                    System.out.println("Indemnity");

                    if (serviceType.equals(ServiceType.CANCELLATION)) {

                        DocumentNumber indemnityNumber = tradeService.getDocumentNumber();
                        DocumentNumber referenceNumber = new DocumentNumber((String) tradeService.getDetails().get("referenceNumber"));

                        // Only BG for migrated data
                        Indemnity indemnity = new Indemnity(indemnityNumber, IndemnityType.BG, referenceNumber);

                        tradeService.getDetails().put("currency", (String) tradeService.getDetails().get("shipmentCurrency"));
                        tradeService.getDetails().put("amount", (String) tradeService.getDetails().get("shipmentAmount"));

                        System.out.println(tradeService.getDetails());

                        indemnity.updateDetails(tradeService.getDetails());

                        indemnity.updateStatus(TradeProductStatus.CANCELLED);

                        TradeProduct indemnityTradeProduct = (TradeProduct)indemnity;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        IndemnityAudit indemnityAudit = new IndemnityAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, indemnityTradeProduct);
                        beanUtilsBean.copyProperties(indemnityAudit, indemnity);

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("1"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.INDEMNITY);
                        tradeProductAudit.setAmount(new BigDecimal((String)tradeService.getDetails().get("shipmentAmount")));
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("shipmentCurrency")));

                        tradeProductAudit.setStatus(indemnityTradeProduct.getStatus());

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        indemnityAudit.setRevId(maxRev);
                        indemnityAudit.setIndemnityNumber(tradeService.getDocumentNumber());
                        indemnityAudit.setCancellationDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("indemnityAudit.getIndemnityNumber() = " + indemnityAudit.getIndemnityNumber());
                        System.out.println("indemnityAudit.getCancellationDate() = " + indemnityAudit.getCancellationDate());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        indemnityAuditDao.insert(indemnityAudit);
                    }
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, readOnly = false)
    public void regenerateNonMigrated() throws Exception {

        List<TradeService> tradeServices = tradeServiceRepository.getAllNonMigratedDataFirstBatch();

        System.out.println("tradeServices.size() = " + tradeServices.size() + "\n");

        BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
        beanUtilsBean.getConvertUtils().register(new DateConverter(null), Date.class);
        beanUtilsBean.getConvertUtils().register(new BigDecimalConverter(null), BigDecimal.class);
        beanUtilsBean.getConvertUtils().register(new SqlTimestampConverter(null), Timestamp.class);

        mainLoop:
        for (TradeService tradeService : tradeServices) {

            DocumentNumber documentNumber = tradeService.getDocumentNumber();
            ServiceType serviceType = tradeService.getServiceType();
            DocumentClass documentClass = tradeService.getDocumentClass();
            DocumentType documentType = tradeService.getDocumentType();
            DocumentSubType1 documentSubType1 = tradeService.getDocumentSubType1();
            DocumentSubType2 documentSubType2 = tradeService.getDocumentSubType2();

            if (documentClass != null) {

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTime(tradeService.getProcessDate());
                long revtstmp = calendar.getTimeInMillis();

                // System.out.println("tradeServiceId = " + tradeService.getTradeServiceId().toString());
                // System.out.println("tradeService.getProcessDate().getTime() = " + tradeService.getProcessDate().getTime());
                // System.out.println("calendar.getTimeZone().toString() = " + calendar.getTimeZone().toString());
                // System.out.println("revtstmp = " + revtstmp);

                RevInfo revInfo = new RevInfo();
                revInfo.setRevtstmp(revtstmp);

                int val = revInfoDao.insert(revInfo);
                int maxRev = revInfoDao.selectMaxRev();

                System.out.println("----------------");
                System.out.println("MAXREV           = " + maxRev);
                System.out.println("documentNumber   = " + documentNumber);
                System.out.println("serviceType      = " + serviceType);
                System.out.println("documentClass    = " + documentClass);
                System.out.println("documentType     = " + documentType);
                System.out.println("documentSubType1 = " + documentSubType1);
                System.out.println("documentSubType2 = " + documentSubType2);
                System.out.println("----------------\n");

                // Non-LC
                if (documentClass.equals(DocumentClass.DA)) {

                    System.out.println("DA");

                    if (serviceType.equals(ServiceType.NEGOTIATION_ACCEPTANCE)) {

                        DocumentAgainstAcceptance da = new DocumentAgainstAcceptance(tradeService.getDocumentNumber(), tradeService.getDetails());
                        String lastTransactionSettlement = buildLastLcTransactionString(ServiceType.NEGOTIATION_ACCEPTANCE, documentClass, documentType);
                        da.updateLastTransaction(lastTransactionSettlement);

                        da.accept(tradeService.getDetails());
                        da.updateStatus(TradeProductStatus.ACCEPTED);

                        TradeProduct daTradeProduct = (TradeProduct)da;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        DocumentAgainstAcceptanceAudit documentAgainstAcceptanceAudit = new DocumentAgainstAcceptanceAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, daTradeProduct);
                        beanUtilsBean.copyProperties(documentAgainstAcceptanceAudit, da);

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("1"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.DA);
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                        tradeProductAudit.setStatus(daTradeProduct.getStatus());

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        documentAgainstAcceptanceAudit.setRevId(maxRev);
                        documentAgainstAcceptanceAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        if(((String)tradeService.getDetails().get("documentType")).equals("FOREIGN")) {
                            documentAgainstAcceptanceAudit.setDocumentType(DocumentType.FOREIGN);
                        } else if(((String)tradeService.getDetails().get("documentType")).equals("DOMESTIC")) {
                            documentAgainstAcceptanceAudit.setDocumentType(DocumentType.DOMESTIC);
                        }
                        documentAgainstAcceptanceAudit.setTsNumber((String)tradeService.getDetails().get("tsNumber"));

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("documentAgainstAcceptanceAudit.getDocumentNumber() = " + documentAgainstAcceptanceAudit.getDocumentNumber());
                        System.out.println("documentAgainstAcceptanceAudit.getMaturityDate() = " + documentAgainstAcceptanceAudit.getMaturityDate());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        documentAgainstAcceptanceAuditDao.insert(documentAgainstAcceptanceAudit);

                    } else if (serviceType.equals(ServiceType.SETTLEMENT)) {

                        DocumentAgainstAcceptance da = new DocumentAgainstAcceptance(tradeService.getDocumentNumber(), tradeService.getDetails());
                        String lastTransactionSettlement = buildLastLcTransactionString(ServiceType.SETTLEMENT, documentClass, documentType);
                        da.updateLastTransaction(lastTransactionSettlement);

                        da.settle(tradeService.getDetails());

                        TradeProduct daTradeProduct = (TradeProduct)da;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        DocumentAgainstAcceptanceAudit documentAgainstAcceptanceAudit = new DocumentAgainstAcceptanceAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, daTradeProduct);
                        beanUtilsBean.copyProperties(documentAgainstAcceptanceAudit, da);

                        if (tradeService.getDetails().get("outstandingAmount") != null && tradeService.getDetails().get("productAmount") != null) {
                            String outstandingAmountStr = (String)tradeService.getDetails().get("outstandingAmount");
                            BigDecimal outstandingAmount = new BigDecimal(outstandingAmountStr.replaceAll(",",""));
                            String productAmountStr = (String)tradeService.getDetails().get("productAmount");
                            BigDecimal productAmount = new BigDecimal(productAmountStr.replaceAll(",",""));
                            BigDecimal diff = outstandingAmount.subtract(productAmount);
                            documentAgainstAcceptanceAudit.setOutstandingAmount(diff);
                            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                                tradeProductAudit.setStatus(TradeProductStatus.ACCEPTED);
                            } else {
                                tradeProductAudit.setStatus(TradeProductStatus.CLOSED);
                            }
                        } else {
                            tradeProductAudit.setStatus(TradeProductStatus.ACCEPTED);
                        }

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("1"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.DA);
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        documentAgainstAcceptanceAudit.setRevId(maxRev);
                        documentAgainstAcceptanceAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        if(((String)tradeService.getDetails().get("documentType")).equals("FOREIGN")) {
                            documentAgainstAcceptanceAudit.setDocumentType(DocumentType.FOREIGN);
                        } else if(((String)tradeService.getDetails().get("documentType")).equals("DOMESTIC")) {
                            documentAgainstAcceptanceAudit.setDocumentType(DocumentType.DOMESTIC);
                        }
                        documentAgainstAcceptanceAudit.setTsNumber((String)tradeService.getDetails().get("tsNumber"));

                        documentAgainstAcceptanceAudit.setSettledDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("documentAgainstAcceptanceAudit.getDocumentNumber() = " + documentAgainstAcceptanceAudit.getDocumentNumber());
                        System.out.println("documentAgainstAcceptanceAudit.getSettledDate() = " + documentAgainstAcceptanceAudit.getSettledDate());
                        System.out.println("tradeProductAudit.getAmount() = " + tradeProductAudit.getAmount().toPlainString());
                        System.out.println("documentAgainstAcceptanceAudit.getOutstandingAmount() = " + documentAgainstAcceptanceAudit.getOutstandingAmount().toPlainString());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        documentAgainstAcceptanceAuditDao.insert(documentAgainstAcceptanceAudit);

                    } else if (serviceType.equals(ServiceType.CANCELLATION)) {

                        DocumentAgainstAcceptance da = new DocumentAgainstAcceptance(tradeService.getDocumentNumber(), tradeService.getDetails());
                        String lastTransactionSettlement = buildLastLcTransactionString(ServiceType.CANCELLATION, documentClass, documentType);
                        da.updateLastTransaction(lastTransactionSettlement);

                        da.cancelDa();

                        TradeProduct daTradeProduct = (TradeProduct)da;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        DocumentAgainstAcceptanceAudit documentAgainstAcceptanceAudit = new DocumentAgainstAcceptanceAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, daTradeProduct);
                        beanUtilsBean.copyProperties(documentAgainstAcceptanceAudit, da);

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("1"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.DA);
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                        tradeProductAudit.setStatus(daTradeProduct.getStatus());

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        documentAgainstAcceptanceAudit.setRevId(maxRev);
                        documentAgainstAcceptanceAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        if(((String)tradeService.getDetails().get("documentType")).equals("FOREIGN")) {
                            documentAgainstAcceptanceAudit.setDocumentType(DocumentType.FOREIGN);
                        } else if(((String)tradeService.getDetails().get("documentType")).equals("DOMESTIC")) {
                            documentAgainstAcceptanceAudit.setDocumentType(DocumentType.DOMESTIC);
                        }
                        documentAgainstAcceptanceAudit.setTsNumber((String)tradeService.getDetails().get("tsNumber"));

                        documentAgainstAcceptanceAudit.setCancelledDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("documentAgainstAcceptanceAudit.getDocumentNumber() = " + documentAgainstAcceptanceAudit.getDocumentNumber());
                        System.out.println("documentAgainstAcceptanceAudit.getCancelledDate() = " + documentAgainstAcceptanceAudit.getCancelledDate());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        documentAgainstAcceptanceAuditDao.insert(documentAgainstAcceptanceAudit);
                    }

                } else if (documentClass.equals(DocumentClass.DP)) {

                    System.out.println("DP");

                    if (serviceType.equals(ServiceType.NEGOTIATION)) {

                        DocumentAgainstPayment dp = new DocumentAgainstPayment(tradeService.getDocumentNumber(), tradeService.getDetails());
                        String lastTransactionSettlement = buildLastLcTransactionString(ServiceType.NEGOTIATION, documentClass, documentType);
                        dp.updateLastTransaction(lastTransactionSettlement);

                        dp.updateStatus(TradeProductStatus.NEGOTIATED);

                        TradeProduct dpTradeProduct = (TradeProduct)dp;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        DocumentAgainstPaymentAudit documentAgainstPaymentAudit = new DocumentAgainstPaymentAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, dpTradeProduct);
                        beanUtilsBean.copyProperties(documentAgainstPaymentAudit, dp);

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("0"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.DP);
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                        tradeProductAudit.setStatus(dpTradeProduct.getStatus());

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        documentAgainstPaymentAudit.setRevId(maxRev);
                        documentAgainstPaymentAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        if(((String)tradeService.getDetails().get("documentType")).equals("FOREIGN")) {
                            documentAgainstPaymentAudit.setDocumentType(DocumentType.FOREIGN);
                        } else if(((String)tradeService.getDetails().get("documentType")).equals("DOMESTIC")) {
                            documentAgainstPaymentAudit.setDocumentType(DocumentType.DOMESTIC);
                        }
                        documentAgainstPaymentAudit.setTsNumber((String)tradeService.getDetails().get("tsNumber"));

                        documentAgainstPaymentAudit.setProcessDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("documentAgainstPaymentAudit.getDocumentNumber() = " + documentAgainstPaymentAudit.getDocumentNumber());
                        System.out.println("documentAgainstPaymentAudit.getProcessDate() = " + documentAgainstPaymentAudit.getProcessDate());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        documentAgainstPaymentAuditDao.insert(documentAgainstPaymentAudit);

                    } else if (serviceType.equals(ServiceType.SETTLEMENT)) {

                        DocumentAgainstPayment dp = new DocumentAgainstPayment(tradeService.getDocumentNumber(), tradeService.getDetails());
                        String lastTransactionSettlement = buildLastLcTransactionString(ServiceType.SETTLEMENT, documentClass, documentType);
                        dp.updateLastTransaction(lastTransactionSettlement);

                        dp.settle(tradeService.getDetails());

                        TradeProduct dpTradeProduct = (TradeProduct)dp;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        DocumentAgainstPaymentAudit documentAgainstPaymentAudit = new DocumentAgainstPaymentAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, dpTradeProduct);
                        beanUtilsBean.copyProperties(documentAgainstPaymentAudit, dp);

                        if (tradeService.getDetails().get("outstandingAmount") != null && tradeService.getDetails().get("productAmount") != null) {
                            String outstandingAmountStr = (String)tradeService.getDetails().get("outstandingAmount");
                            BigDecimal outstandingAmount = new BigDecimal(outstandingAmountStr.replaceAll(",",""));
                            String productAmountStr = (String)tradeService.getDetails().get("productAmount");
                            BigDecimal productAmount = new BigDecimal(productAmountStr.replaceAll(",",""));
                            BigDecimal diff = outstandingAmount.subtract(productAmount);
                            documentAgainstPaymentAudit.setOutstandingAmount(diff);
                            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                                tradeProductAudit.setStatus(TradeProductStatus.NEGOTIATED);
                            } else {
                                tradeProductAudit.setStatus(TradeProductStatus.CLOSED);
                            }
                        } else {
                            tradeProductAudit.setStatus(TradeProductStatus.NEGOTIATED);
                        }

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("1"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.DP);
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        documentAgainstPaymentAudit.setRevId(maxRev);
                        documentAgainstPaymentAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        if(((String)tradeService.getDetails().get("documentType")).equals("FOREIGN")) {
                            documentAgainstPaymentAudit.setDocumentType(DocumentType.FOREIGN);
                        } else if(((String)tradeService.getDetails().get("documentType")).equals("DOMESTIC")) {
                            documentAgainstPaymentAudit.setDocumentType(DocumentType.DOMESTIC);
                        }
                        documentAgainstPaymentAudit.setTsNumber((String)tradeService.getDetails().get("tsNumber"));

                        documentAgainstPaymentAudit.setSettledDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("documentAgainstPaymentAudit.getDocumentNumber() = " + documentAgainstPaymentAudit.getDocumentNumber());
                        System.out.println("documentAgainstPaymentAudit.getSettledDate() = " + documentAgainstPaymentAudit.getSettledDate());
                        System.out.println("tradeProductAudit.getAmount() = " + tradeProductAudit.getAmount().toPlainString());
                        System.out.println("documentAgainstPaymentAudit.getOutstandingAmount() = " + documentAgainstPaymentAudit.getOutstandingAmount().toPlainString());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        documentAgainstPaymentAuditDao.insert(documentAgainstPaymentAudit);

                    } else if (serviceType.equals(ServiceType.CANCELLATION)) {

                        DocumentAgainstPayment dp = new DocumentAgainstPayment(tradeService.getDocumentNumber(), tradeService.getDetails());
                        String lastTransactionSettlement = buildLastLcTransactionString(ServiceType.CANCELLATION, documentClass, documentType);
                        dp.updateLastTransaction(lastTransactionSettlement);

                        dp.cancelDp();

                        TradeProduct daTradeProduct = (TradeProduct)dp;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        DocumentAgainstPaymentAudit documentAgainstPaymentAudit = new DocumentAgainstPaymentAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, daTradeProduct);
                        beanUtilsBean.copyProperties(documentAgainstPaymentAudit, dp);

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("1"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.DP);
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                        tradeProductAudit.setStatus(daTradeProduct.getStatus());

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        documentAgainstPaymentAudit.setRevId(maxRev);
                        documentAgainstPaymentAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        if(((String)tradeService.getDetails().get("documentType")).equals("FOREIGN")) {
                            documentAgainstPaymentAudit.setDocumentType(DocumentType.FOREIGN);
                        } else if(((String)tradeService.getDetails().get("documentType")).equals("DOMESTIC")) {
                            documentAgainstPaymentAudit.setDocumentType(DocumentType.DOMESTIC);
                        }
                        documentAgainstPaymentAudit.setTsNumber((String)tradeService.getDetails().get("tsNumber"));

                        documentAgainstPaymentAudit.setCancelledDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("documentAgainstPaymentAudit.getDocumentNumber() = " + documentAgainstPaymentAudit.getDocumentNumber());
                        System.out.println("documentAgainstPaymentAudit.getCancelledDate() = " + documentAgainstPaymentAudit.getCancelledDate());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        documentAgainstPaymentAuditDao.insert(documentAgainstPaymentAudit);
                    }

                } else if (documentClass.equals(DocumentClass.OA)) {

                    System.out.println("OA");

                    if (serviceType.equals(ServiceType.NEGOTIATION)) {

                        OpenAccount oa = new OpenAccount(tradeService.getDocumentNumber(), tradeService.getDetails());
                        String lastTransactionSettlement = buildLastLcTransactionString(ServiceType.NEGOTIATION, documentClass, documentType);
                        oa.updateLastTransaction(lastTransactionSettlement);

                        oa.updateStatus(TradeProductStatus.NEGOTIATED);

                        TradeProduct oaTradeProduct = (TradeProduct)oa;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        OpenAccountAudit openAccountAudit = new OpenAccountAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, oaTradeProduct);
                        beanUtilsBean.copyProperties(openAccountAudit, oa);

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("0"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.OA);
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                        tradeProductAudit.setStatus(oaTradeProduct.getStatus());

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        openAccountAudit.setRevId(maxRev);
                        openAccountAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        if(((String)tradeService.getDetails().get("documentType")).equals("FOREIGN")) {
                            openAccountAudit.setDocumentType(DocumentType.FOREIGN);
                        } else if(((String)tradeService.getDetails().get("documentType")).equals("DOMESTIC")) {
                            openAccountAudit.setDocumentType(DocumentType.DOMESTIC);
                        }
                        openAccountAudit.setTsNumber((String)tradeService.getDetails().get("tsNumber"));

                        openAccountAudit.setProcessDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("openAccountAudit.getDocumentNumber() = " + openAccountAudit.getDocumentNumber());
                        System.out.println("openAccountAudit.setProcessDate() = " + openAccountAudit.getProcessDate());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        openAccountAuditDao.insert(openAccountAudit);

                    } else if (serviceType.equals(ServiceType.SETTLEMENT)) {

                        OpenAccount oa = new OpenAccount(tradeService.getDocumentNumber(), tradeService.getDetails());
                        String lastTransactionSettlement = buildLastLcTransactionString(ServiceType.SETTLEMENT, documentClass, documentType);
                        oa.updateLastTransaction(lastTransactionSettlement);

                        oa.settle(tradeService.getDetails());

                        TradeProduct oaTradeProduct = (TradeProduct)oa;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        OpenAccountAudit openAccountAudit = new OpenAccountAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, oaTradeProduct);
                        beanUtilsBean.copyProperties(openAccountAudit, oa);

                        if (tradeService.getDetails().get("outstandingAmount") != null && tradeService.getDetails().get("productAmount") != null) {
                            String outstandingAmountStr = (String)tradeService.getDetails().get("outstandingAmount");
                            BigDecimal outstandingAmount = new BigDecimal(outstandingAmountStr.replaceAll(",",""));
                            String productAmountStr = (String)tradeService.getDetails().get("productAmount");
                            BigDecimal productAmount = new BigDecimal(productAmountStr.replaceAll(",",""));
                            BigDecimal diff = outstandingAmount.subtract(productAmount);
                            openAccountAudit.setOutstandingAmount(diff);
                            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                                tradeProductAudit.setStatus(TradeProductStatus.NEGOTIATED);
                            } else {
                                tradeProductAudit.setStatus(TradeProductStatus.CLOSED);
                            }
                        } else {
                            tradeProductAudit.setStatus(TradeProductStatus.NEGOTIATED);
                        }

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("1"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.OA);
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        openAccountAudit.setRevId(maxRev);
                        openAccountAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        if(((String)tradeService.getDetails().get("documentType")).equals("FOREIGN")) {
                            openAccountAudit.setDocumentType(DocumentType.FOREIGN);
                        } else if(((String)tradeService.getDetails().get("documentType")).equals("DOMESTIC")) {
                            openAccountAudit.setDocumentType(DocumentType.DOMESTIC);
                        }
                        openAccountAudit.setTsNumber((String)tradeService.getDetails().get("tsNumber"));

                        openAccountAudit.setSettledDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("openAccountAudit.getDocumentNumber() = " + openAccountAudit.getDocumentNumber());
                        System.out.println("openAccountAudit.getSettledDate() = " + openAccountAudit.getSettledDate());
                        System.out.println("tradeProductAudit.getAmount() = " + tradeProductAudit.getAmount().toPlainString());
                        System.out.println("openAccountAudit.getOutstandingAmount() = " + openAccountAudit.getOutstandingAmount().toPlainString());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        openAccountAuditDao.insert(openAccountAudit);

                    } else if (serviceType.equals(ServiceType.CANCELLATION)) {

                        OpenAccount oa = new OpenAccount(tradeService.getDocumentNumber(), tradeService.getDetails());
                        String lastTransactionSettlement = buildLastLcTransactionString(ServiceType.CANCELLATION, documentClass, documentType);
                        oa.updateLastTransaction(lastTransactionSettlement);

                        oa.cancelOa();

                        TradeProduct oaTradeProduct = (TradeProduct)oa;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        OpenAccountAudit openAccountAudit = new OpenAccountAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, oaTradeProduct);
                        beanUtilsBean.copyProperties(openAccountAudit, oa);

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("1"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.OA);
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                        tradeProductAudit.setStatus(oaTradeProduct.getStatus());

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        openAccountAudit.setRevId(maxRev);
                        openAccountAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        if(((String)tradeService.getDetails().get("documentType")).equals("FOREIGN")) {
                            openAccountAudit.setDocumentType(DocumentType.FOREIGN);
                        } else if(((String)tradeService.getDetails().get("documentType")).equals("DOMESTIC")) {
                            openAccountAudit.setDocumentType(DocumentType.DOMESTIC);
                        }
                        openAccountAudit.setTsNumber((String)tradeService.getDetails().get("tsNumber"));

                        openAccountAudit.setCancelledDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("openAccountAudit.getDocumentNumber() = " + openAccountAudit.getDocumentNumber());
                        System.out.println("openAccountAudit.getCancelledDate() = " + openAccountAudit.getCancelledDate());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        openAccountAuditDao.insert(openAccountAudit);
                    }

                // Export Bills for Collection
                } else if (documentClass.equals(DocumentClass.BC)) {

                    System.out.println("Export Bills");

                    if (documentType.equals(DocumentType.FOREIGN)) {

                        if (serviceType.equals(ServiceType.NEGOTIATION)) {

                            ExportBills exportBills = new ExportBills(tradeService.getDocumentNumber(), tradeService.getDetails(), ProductType.BC, ExportBillType.EBC);

                            exportBills.setLoanDetails(tradeService.getDetails());

                            if (tradeService.getDetails().get("paymentMode") != null && "LC".equals((String) tradeService.getDetails().get("paymentMode"))) {
                                exportBills.setLcDetails(tradeService.getDetails());
                            } else if (tradeService.getDetails().get("paymentMode") != null &&
                                    ("DA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DP".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "OA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DR".equals((String) tradeService.getDetails().get("paymentMode")))) {
                                exportBills.setNonLcDetails(tradeService.getDetails());
                            }

                            BigDecimal totalAmountClaimedAmount = BigDecimal.ZERO;
                            Date totalAmountClaimedDate = null;
                            Currency totalAmountClaimedCurrency = null;

                            if ("A".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yyyy");

                                if (tradeService.getDetails().get("totalAmountClaimedDate") != null) {
                                    totalAmountClaimedDate = simpleDateFormat.parse((String) tradeService.getDetails().get("totalAmountClaimedDate"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedA") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedA"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyA") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyA"));
                                }

                            } else if ("B".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                if (tradeService.getDetails().get("totalAmountClaimedB") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedB"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyB") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyB"));
                                }
                            }

                            exportBills.setTotalAmountDetails(totalAmountClaimedDate, totalAmountClaimedAmount, totalAmountClaimedCurrency);

                            exportBills.updateStatus(TradeProductStatus.NEGOTIATED);

                            TradeProduct exportBillsTradeProduct = (TradeProduct)exportBills;
                            TradeProductAudit tradeProductAudit = new TradeProductAudit();
                            ExportBillsAudit exportBillsAudit = new ExportBillsAudit();

                            beanUtilsBean.copyProperties(tradeProductAudit, exportBillsTradeProduct);
                            beanUtilsBean.copyProperties(exportBillsAudit, exportBills);

                            tradeProductAudit.setRevId(maxRev);
                            tradeProductAudit.setRevType(Short.valueOf("0"));
                            tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                            tradeProductAudit.setProductType(ProductType.EXPORT_BILLS);
                            tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                            tradeProductAudit.setStatus(exportBillsTradeProduct.getStatus());

                            tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                            tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                            tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                            tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                            exportBillsAudit.setRevId(maxRev);
                            exportBillsAudit.setDocumentNumber(tradeService.getDocumentNumber());
                            exportBillsAudit.setProcessDate(tradeService.getProcessDate());
                            exportBillsAudit.setAccountType(exportBills.getAccountType());
                            exportBillsAudit.setNegotiationDate(exportBills.getNegotiationDate());

                            System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                            System.out.println("exportBillsAudit.getDocumentNumber() = " + exportBillsAudit.getDocumentNumber());
                            System.out.println("exportBillsAudit.getNegotiationDate() = " + exportBillsAudit.getNegotiationDate());
                            System.out.println("exportBillsAudit.getProcessDate() = " + exportBillsAudit.getProcessDate());

                            tradeProductAuditDao.insert(tradeProductAudit);
                            exportBillsAuditDao.insert(exportBillsAudit);

                        } else if (serviceType.equals(ServiceType.SETTLEMENT)) {

                            ExportBills exportBills = new ExportBills(tradeService.getDocumentNumber(), tradeService.getDetails(), ProductType.BC, ExportBillType.EBC);

                            exportBills.setLoanDetails(tradeService.getDetails());

                            if (tradeService.getDetails().get("paymentMode") != null && "LC".equals((String) tradeService.getDetails().get("paymentMode"))) {
                                exportBills.setLcDetails(tradeService.getDetails());
                            } else if (tradeService.getDetails().get("paymentMode") != null &&
                                    ("DA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DP".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "OA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DR".equals((String) tradeService.getDetails().get("paymentMode")))) {
                                exportBills.setNonLcDetails(tradeService.getDetails());
                            }

                            BigDecimal totalAmountClaimedAmount = BigDecimal.ZERO;
                            Date totalAmountClaimedDate = null;
                            Currency totalAmountClaimedCurrency = null;

                            if ("A".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yyyy");

                                if (tradeService.getDetails().get("totalAmountClaimedDate") != null) {
                                    totalAmountClaimedDate = simpleDateFormat.parse((String) tradeService.getDetails().get("totalAmountClaimedDate"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedA") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedA"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyA") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyA"));
                                }

                            } else if ("B".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                if (tradeService.getDetails().get("totalAmountClaimedB") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedB"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyB") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyB"));
                                }
                            }

                            exportBills.setTotalAmountDetails(totalAmountClaimedDate, totalAmountClaimedAmount, totalAmountClaimedCurrency);

                            exportBills.settleExportBills(new BigDecimal((String) tradeService.getDetails().get("proceedsAmount")),
                                    (String) tradeService.getDetails().get("partialNego"));

                            exportBills.updateDetails(tradeService.getDetails());

                            exportBills.updateStatus(TradeProductStatus.SETTLED);

                            TradeProduct exportBillsTradeProduct = (TradeProduct)exportBills;
                            TradeProductAudit tradeProductAudit = new TradeProductAudit();
                            ExportBillsAudit exportBillsAudit = new ExportBillsAudit();

                            beanUtilsBean.copyProperties(tradeProductAudit, exportBillsTradeProduct);
                            beanUtilsBean.copyProperties(exportBillsAudit, exportBills);

                            tradeProductAudit.setRevId(maxRev);
                            tradeProductAudit.setRevType(Short.valueOf("1"));
                            tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                            tradeProductAudit.setProductType(ProductType.EXPORT_BILLS);
                            tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                            tradeProductAudit.setStatus(exportBillsTradeProduct.getStatus());

                            tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                            tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                            tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                            tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                            exportBillsAudit.setRevId(maxRev);
                            exportBillsAudit.setDocumentNumber(tradeService.getDocumentNumber());
                            exportBillsAudit.setSettlementDate(tradeService.getProcessDate());
                            exportBillsAudit.setAccountType(exportBills.getAccountType());
                            exportBillsAudit.setNegotiationDate(exportBills.getNegotiationDate());

                            System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                            System.out.println("exportBillsAudit.getDocumentNumber() = " + exportBillsAudit.getDocumentNumber());
                            System.out.println("exportBillsAudit.getNegotiationDate() = " + exportBillsAudit.getNegotiationDate());
                            System.out.println("exportBillsAudit.getSettledDate() = " + exportBillsAudit.getSettlementDate());

                            tradeProductAuditDao.insert(tradeProductAudit);
                            exportBillsAuditDao.insert(exportBillsAudit);

                        } else if (serviceType.equals(ServiceType.CANCELLATION)) {

                            ExportBills exportBills = new ExportBills(tradeService.getDocumentNumber(), tradeService.getDetails(), ProductType.BC, ExportBillType.EBC);

                            exportBills.setLoanDetails(tradeService.getDetails());

                            if (tradeService.getDetails().get("paymentMode") != null && "LC".equals((String) tradeService.getDetails().get("paymentMode"))) {
                                exportBills.setLcDetails(tradeService.getDetails());
                            } else if (tradeService.getDetails().get("paymentMode") != null &&
                                    ("DA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DP".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "OA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DR".equals((String) tradeService.getDetails().get("paymentMode")))) {
                                exportBills.setNonLcDetails(tradeService.getDetails());
                            }

                            BigDecimal totalAmountClaimedAmount = BigDecimal.ZERO;
                            Date totalAmountClaimedDate = null;
                            Currency totalAmountClaimedCurrency = null;

                            if ("A".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yyyy");

                                if (tradeService.getDetails().get("totalAmountClaimedDate") != null) {
                                    totalAmountClaimedDate = simpleDateFormat.parse((String) tradeService.getDetails().get("totalAmountClaimedDate"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedA") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedA"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyA") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyA"));
                                }

                            } else if ("B".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                if (tradeService.getDetails().get("totalAmountClaimedB") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedB"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyB") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyB"));
                                }
                            }

                            exportBills.setTotalAmountDetails(totalAmountClaimedDate, totalAmountClaimedAmount, totalAmountClaimedCurrency);

                            exportBills.cancelExportBills((String) tradeService.getDetails().get("reasonForCancellation"));

                            exportBills.updateDetails(tradeService.getDetails());

                            exportBills.updateStatus(TradeProductStatus.CANCELLED);

                            TradeProduct exportBillsTradeProduct = (TradeProduct)exportBills;
                            TradeProductAudit tradeProductAudit = new TradeProductAudit();
                            ExportBillsAudit exportBillsAudit = new ExportBillsAudit();

                            beanUtilsBean.copyProperties(tradeProductAudit, exportBillsTradeProduct);
                            beanUtilsBean.copyProperties(exportBillsAudit, exportBills);

                            tradeProductAudit.setRevId(maxRev);
                            tradeProductAudit.setRevType(Short.valueOf("1"));
                            tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                            tradeProductAudit.setProductType(ProductType.EXPORT_BILLS);
                            tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                            tradeProductAudit.setStatus(exportBillsTradeProduct.getStatus());

                            tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                            tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                            tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                            tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                            exportBillsAudit.setRevId(maxRev);
                            exportBillsAudit.setDocumentNumber(tradeService.getDocumentNumber());
                            exportBillsAudit.setSettlementDate(tradeService.getProcessDate());
                            exportBillsAudit.setAccountType(exportBills.getAccountType());
                            exportBillsAudit.setNegotiationDate(exportBills.getNegotiationDate());

                            System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                            System.out.println("exportBillsAudit.getDocumentNumber() = " + exportBillsAudit.getDocumentNumber());
                            System.out.println("exportBillsAudit.getNegotiationDate() = " + exportBillsAudit.getNegotiationDate());
                            System.out.println("exportBillsAudit cancelled date = " + exportBillsAudit.getSettlementDate());

                            tradeProductAuditDao.insert(tradeProductAudit);
                            exportBillsAuditDao.insert(exportBillsAudit);
                        }

                    } else if (documentType.equals(DocumentType.DOMESTIC)) {

                        if (serviceType.equals(ServiceType.NEGOTIATION)) {

                            ExportBills exportBills = new ExportBills(tradeService.getDocumentNumber(), tradeService.getDetails(), ProductType.BC, ExportBillType.DBC);

                            exportBills.setLoanDetails(tradeService.getDetails());

                            if (tradeService.getDetails().get("paymentMode") != null && "LC".equals((String) tradeService.getDetails().get("paymentMode"))) {
                                exportBills.setLcDetails(tradeService.getDetails());
                            } else if (tradeService.getDetails().get("paymentMode") != null &&
                                    ("DA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DP".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "OA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DR".equals((String) tradeService.getDetails().get("paymentMode")))) {
                                exportBills.setNonLcDetails(tradeService.getDetails());
                            }

                            BigDecimal totalAmountClaimedAmount = BigDecimal.ZERO;
                            Date totalAmountClaimedDate = null;
                            Currency totalAmountClaimedCurrency = null;

                            if ("A".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yyyy");

                                if (tradeService.getDetails().get("totalAmountClaimedDate") != null) {
                                    totalAmountClaimedDate = simpleDateFormat.parse((String) tradeService.getDetails().get("totalAmountClaimedDate"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedA") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedA"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyA") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyA"));
                                }

                            } else if ("B".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                if (tradeService.getDetails().get("totalAmountClaimedB") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedB"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyB") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyB"));
                                }
                            }

                            exportBills.setTotalAmountDetails(totalAmountClaimedDate, totalAmountClaimedAmount, totalAmountClaimedCurrency);

                            exportBills.updateStatus(TradeProductStatus.NEGOTIATED);

                            TradeProduct exportBillsTradeProduct = (TradeProduct)exportBills;
                            TradeProductAudit tradeProductAudit = new TradeProductAudit();
                            ExportBillsAudit exportBillsAudit = new ExportBillsAudit();

                            beanUtilsBean.copyProperties(tradeProductAudit, exportBillsTradeProduct);
                            beanUtilsBean.copyProperties(exportBillsAudit, exportBills);

                            tradeProductAudit.setRevId(maxRev);
                            tradeProductAudit.setRevType(Short.valueOf("0"));
                            tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                            tradeProductAudit.setProductType(ProductType.EXPORT_BILLS);
                            tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                            tradeProductAudit.setStatus(exportBillsTradeProduct.getStatus());

                            tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                            tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                            tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                            tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                            exportBillsAudit.setRevId(maxRev);
                            exportBillsAudit.setDocumentNumber(tradeService.getDocumentNumber());
                            exportBillsAudit.setProcessDate(tradeService.getProcessDate());
                            exportBillsAudit.setAccountType(exportBills.getAccountType());

                            System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                            System.out.println("exportBillsAudit.getDocumentNumber() = " + exportBillsAudit.getDocumentNumber());
                            System.out.println("exportBillsAudit.getProcessDate() = " + exportBillsAudit.getProcessDate());

                            tradeProductAuditDao.insert(tradeProductAudit);
                            exportBillsAuditDao.insert(exportBillsAudit);

                        } else if (serviceType.equals(ServiceType.SETTLEMENT)) {

                            ExportBills exportBills = new ExportBills(tradeService.getDocumentNumber(), tradeService.getDetails(), ProductType.BC, ExportBillType.DBC);

                            exportBills.setLoanDetails(tradeService.getDetails());

                            if (tradeService.getDetails().get("paymentMode") != null && "LC".equals((String) tradeService.getDetails().get("paymentMode"))) {
                                exportBills.setLcDetails(tradeService.getDetails());
                            } else if (tradeService.getDetails().get("paymentMode") != null &&
                                    ("DA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DP".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "OA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DR".equals((String) tradeService.getDetails().get("paymentMode")))) {
                                exportBills.setNonLcDetails(tradeService.getDetails());
                            }

                            BigDecimal totalAmountClaimedAmount = BigDecimal.ZERO;
                            Date totalAmountClaimedDate = null;
                            Currency totalAmountClaimedCurrency = null;

                            if ("A".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yyyy");

                                if (tradeService.getDetails().get("totalAmountClaimedDate") != null) {
                                    totalAmountClaimedDate = simpleDateFormat.parse((String) tradeService.getDetails().get("totalAmountClaimedDate"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedA") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedA"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyA") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyA"));
                                }

                            } else if ("B".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                if (tradeService.getDetails().get("totalAmountClaimedB") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedB"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyB") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyB"));
                                }
                            }

                            exportBills.setTotalAmountDetails(totalAmountClaimedDate, totalAmountClaimedAmount, totalAmountClaimedCurrency);

                            exportBills.settleExportBills(new BigDecimal((String) tradeService.getDetails().get("proceedsAmount")),
                                                          (String) tradeService.getDetails().get("partialNego"));

                            exportBills.updateDetails(tradeService.getDetails());

                            exportBills.updateStatus(TradeProductStatus.SETTLED);

                            TradeProduct exportBillsTradeProduct = (TradeProduct)exportBills;
                            TradeProductAudit tradeProductAudit = new TradeProductAudit();
                            ExportBillsAudit exportBillsAudit = new ExportBillsAudit();

                            beanUtilsBean.copyProperties(tradeProductAudit, exportBillsTradeProduct);
                            beanUtilsBean.copyProperties(exportBillsAudit, exportBills);

                            tradeProductAudit.setRevId(maxRev);
                            tradeProductAudit.setRevType(Short.valueOf("1"));
                            tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                            tradeProductAudit.setProductType(ProductType.EXPORT_BILLS);
                            tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                            tradeProductAudit.setStatus(exportBillsTradeProduct.getStatus());

                            tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                            tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                            tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                            tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                            exportBillsAudit.setRevId(maxRev);
                            exportBillsAudit.setDocumentNumber(tradeService.getDocumentNumber());
                            exportBillsAudit.setSettlementDate(tradeService.getProcessDate());
                            exportBillsAudit.setAccountType(exportBills.getAccountType());

                            System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                            System.out.println("exportBillsAudit.getDocumentNumber() = " + exportBillsAudit.getDocumentNumber());
                            System.out.println("exportBillsAudit.getSettledDate() = " + exportBillsAudit.getSettlementDate());

                            tradeProductAuditDao.insert(tradeProductAudit);
                            exportBillsAuditDao.insert(exportBillsAudit);

                        } else if (serviceType.equals(ServiceType.CANCELLATION)) {

                            ExportBills exportBills = new ExportBills(tradeService.getDocumentNumber(), tradeService.getDetails(), ProductType.BC, ExportBillType.EBC);

                            exportBills.setLoanDetails(tradeService.getDetails());

                            if (tradeService.getDetails().get("paymentMode") != null && "LC".equals((String) tradeService.getDetails().get("paymentMode"))) {
                                exportBills.setLcDetails(tradeService.getDetails());
                            } else if (tradeService.getDetails().get("paymentMode") != null &&
                                    ("DA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DP".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "OA".equals((String) tradeService.getDetails().get("paymentMode")) ||
                                     "DR".equals((String) tradeService.getDetails().get("paymentMode")))) {
                                exportBills.setNonLcDetails(tradeService.getDetails());
                            }

                            BigDecimal totalAmountClaimedAmount = BigDecimal.ZERO;
                            Date totalAmountClaimedDate = null;
                            Currency totalAmountClaimedCurrency = null;

                            if ("A".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yyyy");

                                if (tradeService.getDetails().get("totalAmountClaimedDate") != null) {
                                    totalAmountClaimedDate = simpleDateFormat.parse((String) tradeService.getDetails().get("totalAmountClaimedDate"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedA") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedA"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyA") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyA"));
                                }

                            } else if ("B".equals(tradeService.getDetails().get("totalAmountClaimedFlag"))) {
                                if (tradeService.getDetails().get("totalAmountClaimedB") != null) {
                                    totalAmountClaimedAmount = new BigDecimal((String) tradeService.getDetails().get("totalAmountClaimedB"));
                                }

                                if (tradeService.getDetails().get("totalAmountClaimedCurrencyB") != null) {
                                    totalAmountClaimedCurrency = Currency.getInstance((String) tradeService.getDetails().get("totalAmountClaimedCurrencyB"));
                                }
                            }

                            exportBills.setTotalAmountDetails(totalAmountClaimedDate, totalAmountClaimedAmount, totalAmountClaimedCurrency);

                            exportBills.cancelExportBills((String) tradeService.getDetails().get("reasonForCancellation"));

                            exportBills.updateDetails(tradeService.getDetails());

                            exportBills.updateStatus(TradeProductStatus.CANCELLED);

                            TradeProduct exportBillsTradeProduct = (TradeProduct)exportBills;
                            TradeProductAudit tradeProductAudit = new TradeProductAudit();
                            ExportBillsAudit exportBillsAudit = new ExportBillsAudit();

                            beanUtilsBean.copyProperties(tradeProductAudit, exportBillsTradeProduct);
                            beanUtilsBean.copyProperties(exportBillsAudit, exportBills);

                            tradeProductAudit.setRevId(maxRev);
                            tradeProductAudit.setRevType(Short.valueOf("1"));
                            tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                            tradeProductAudit.setProductType(ProductType.EXPORT_BILLS);
                            tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("currency")));

                            tradeProductAudit.setStatus(exportBillsTradeProduct.getStatus());

                            tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                            tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                            tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                            tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                            exportBillsAudit.setRevId(maxRev);
                            exportBillsAudit.setDocumentNumber(tradeService.getDocumentNumber());
                            exportBillsAudit.setSettlementDate(tradeService.getProcessDate());
                            exportBillsAudit.setAccountType(exportBills.getAccountType());

                            System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                            System.out.println("exportBillsAudit.getDocumentNumber() = " + exportBillsAudit.getDocumentNumber());
                            System.out.println("exportBillsAudit cancelled date = " + exportBillsAudit.getSettlementDate());

                            tradeProductAuditDao.insert(tradeProductAudit);
                            exportBillsAuditDao.insert(exportBillsAudit);
                        }
                    }

                // Indemnity
                } else if (documentClass.equals(DocumentClass.INDEMNITY)) {

                    System.out.println("Indemnity");

                    if (serviceType.equals(ServiceType.ISSUANCE)) {

                        Indemnity indemnity = null;
                        DocumentNumber indemnityNumber = tradeService.getDocumentNumber();
                        DocumentNumber referenceNumber = new DocumentNumber((String) tradeService.getDetails().get("referenceNumber"));

                        String indemnityType = (String) tradeService.getDetails().get("indemnityType");
                        if (indemnityType.equals("BG")) {
                            indemnity = new Indemnity(indemnityNumber, IndemnityType.BG, referenceNumber);
                            System.out.println("BG persisting...");
                        } else if (indemnityType.equals("BE")) {
                            indemnity = new Indemnity(indemnityNumber, IndemnityType.BE, referenceNumber);
                            System.out.println("BE persisting...");
                        }

                        tradeService.getDetails().put("currency", (String) tradeService.getDetails().get("shipmentCurrency"));
                        tradeService.getDetails().put("amount", (String) tradeService.getDetails().get("shipmentAmount"));

                        System.out.println(tradeService.getDetails());

                        indemnity.updateDetails(tradeService.getDetails());

                        indemnity.updateStatus(TradeProductStatus.OPEN);

                        TradeProduct indemnityTradeProduct = (TradeProduct)indemnity;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        IndemnityAudit indemnityAudit = new IndemnityAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, indemnityTradeProduct);
                        beanUtilsBean.copyProperties(indemnityAudit, indemnity);

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("0"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.INDEMNITY);
                        tradeProductAudit.setAmount(new BigDecimal((String)tradeService.getDetails().get("shipmentAmount")));
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("shipmentCurrency")));

                        tradeProductAudit.setStatus(indemnityTradeProduct.getStatus());

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        indemnityAudit.setRevId(maxRev);
                        indemnityAudit.setIndemnityNumber(tradeService.getDocumentNumber());
                        indemnityAudit.setProcessDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("indemnityAudit.getIndemnityNumber() = " + indemnityAudit.getIndemnityNumber());
                        System.out.println("indemnityAudit.getProcessDate() = " + indemnityAudit.getProcessDate());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        indemnityAuditDao.insert(indemnityAudit);

                    } else if (serviceType.equals(ServiceType.CANCELLATION)) {

                        Indemnity indemnity = null;
                        DocumentNumber indemnityNumber = tradeService.getDocumentNumber();
                        DocumentNumber referenceNumber = new DocumentNumber((String) tradeService.getDetails().get("referenceNumber"));

                        String indemnityType = (String) tradeService.getDetails().get("indemnityType");
                        if (indemnityType.equals("BG")) {
                            indemnity = new Indemnity(indemnityNumber, IndemnityType.BG, referenceNumber);
                            System.out.println("BG persisting...");
                        } else if (indemnityType.equals("BE")) {
                            indemnity = new Indemnity(indemnityNumber, IndemnityType.BE, referenceNumber);
                            System.out.println("BE persisting...");
                        }

                        tradeService.getDetails().put("currency", (String) tradeService.getDetails().get("shipmentCurrency"));
                        tradeService.getDetails().put("amount", (String) tradeService.getDetails().get("shipmentAmount"));

                        System.out.println(tradeService.getDetails());

                        indemnity.updateDetails(tradeService.getDetails());

                        indemnity.updateStatus(TradeProductStatus.CANCELLED);

                        TradeProduct indemnityTradeProduct = (TradeProduct)indemnity;
                        TradeProductAudit tradeProductAudit = new TradeProductAudit();
                        IndemnityAudit indemnityAudit = new IndemnityAudit();

                        beanUtilsBean.copyProperties(tradeProductAudit, indemnityTradeProduct);
                        beanUtilsBean.copyProperties(indemnityAudit, indemnity);

                        tradeProductAudit.setRevId(maxRev);
                        tradeProductAudit.setRevType(Short.valueOf("1"));
                        tradeProductAudit.setDocumentNumber(tradeService.getDocumentNumber());
                        tradeProductAudit.setProductType(ProductType.INDEMNITY);
                        tradeProductAudit.setAmount(new BigDecimal((String)tradeService.getDetails().get("shipmentAmount")));
                        tradeProductAudit.setCurrency(Currency.getInstance((String)tradeService.getDetails().get("shipmentCurrency")));

                        tradeProductAudit.setStatus(indemnityTradeProduct.getStatus());

                        tradeProductAudit.setAccountOfficer((String)tradeService.getDetails().get("accountOfficer"));
                        tradeProductAudit.setCcbdBranchUnitCode((String)tradeService.getDetails().get("ccbdBranchUnitCode"));
                        tradeProductAudit.setOfficerCode((String)tradeService.getDetails().get("officerCode"));
                        tradeProductAudit.setExceptionCode((String)tradeService.getDetails().get("exceptionCode"));

                        indemnityAudit.setRevId(maxRev);
                        indemnityAudit.setIndemnityNumber(tradeService.getDocumentNumber());
                        indemnityAudit.setCancellationDate(tradeService.getProcessDate());

                        System.out.println("tradeProductAudit.getDocumentNumber() = " + tradeProductAudit.getDocumentNumber());
                        System.out.println("indemnityAudit.getIndemnityNumber() = " + indemnityAudit.getIndemnityNumber());
                        System.out.println("indemnityAudit.getCancellationDate() = " + indemnityAudit.getCancellationDate());

                        tradeProductAuditDao.insert(tradeProductAudit);
                        indemnityAuditDao.insert(indemnityAudit);
                    }
                }
            }
        }
    }

    private String buildLastLcTransactionString(ServiceType serviceType,
                                                DocumentClass documentClass, DocumentType documentType) {

        String docTypeStr = "FX";

        System.out.println("serviceType.toString() >> " + serviceType.toString());
        System.out.println("documentClass.toString() >> " + documentClass.toString());
        System.out.println("documentType.toString() >> " + documentType.toString());

        if (documentType.equals(DocumentType.DOMESTIC)) {
            docTypeStr = "DM";
        }

        StringBuilder builder = new StringBuilder("");
        builder.append(docTypeStr);

        switch (documentClass) {
            case DA:
            case DP:
            case OA:
            case DR:
                builder.append(" ");
                builder.append(documentClass.toString().toUpperCase());
                break;
        }

        builder.append(" ");
        builder.append(WordUtils.capitalizeFully(serviceType.toString().replaceAll("_", " ")));
        System.out.println("builder.toString() >> " + builder.toString());
        return builder.toString();
    }
}
