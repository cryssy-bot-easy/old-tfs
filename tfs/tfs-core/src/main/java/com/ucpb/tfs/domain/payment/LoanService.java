package com.ucpb.tfs.domain.payment;

import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ucpb.tfs.batch.report.dw.DocumentClass;
import com.ucpb.tfs.domain.payment.event.PaymentItemPaidEvent;
import com.ucpb.tfs.domain.payment.event.PaymentItemPaymentReversedEvent;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.TradeProduct;
import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType2;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.interfaces.domain.Loan;
import com.ucpb.tfs.interfaces.services.FacilityService;
import com.ucpb.tfs.interfaces.services.exception.LoanAlreadyReleasedException;
import com.ucpb.tfs.interfaces.services.impl.NonExistentLoanException;
import com.ucpb.tfs.utils.DateUtil;
import org.codehaus.plexus.util.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.Days;

import static com.ucpb.tfs.domain.service.enumTypes.DocumentClass.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

/**
 */
@Component
@Transactional
public class LoanService {

    private static final String DEFAULT_TERM_CODE = "D";
    private static final int DEFAULT_GROUP_CODE = 180;
    private static final String UNLINK_FLAG = "N";
    private static final BigDecimal DIVISOR = new BigDecimal("100");
    private static final String LOAN_TERM = "loanTerm";
    private static final String LOAN_MATURITY_DATE = "loanMaturityDate";
    private static final String PAYMENT_TERM = "paymentTerm";
    private static final String PAYMENT_CODE = "paymentCode";
    private static final String FACILITY_TYPE = "facilityType";
    private static final String FACILITY_ID = "facilityId";
    private static final String MODE_OF_PAYMENT = "modeOfPayment";
    private static final String LC_NUMBER = "lcNumber";
    private static final String TRADE_SERVICE_ID = "tradeServiceId";
    private static final String REPRICING_TERM = "repricingTerm";
    private static final String AMOUNT = "amount";
    private static final String FACILITY_REFERENCE_NUMBER = "facilityReferenceNumber";
    private static final String NEGOTIATION_VALUE_DATE = "negotiationValueDate";
    private static final String VALUE_DATE = "valueDate";
    private static final String CRAM_APPROVAL_FLAG = "withCramApproval";

    private static final String ERROR_MESSAGE = "ERRDSC";


    @Autowired
    private TradeProductRepository tradeProductRepository;

    @Autowired
    private com.ucpb.tfs.interfaces.services.LoanService interfaceLoanService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Autowired(required = false)
    private String silverlakeDate;
    
    @Autowired
    private FacilityService facilityService;


    public long createLoan(com.ucpb.tfs.domain.payment.modes.Loan tfsLoan,String transactingUser, PaymentDetail detail, TradeService tradeService) {

        String documentNumber = "";

        if (LC.equals(tradeService.getDocumentClass())) {
            documentNumber = (String) tradeService.getDetails().get(LC_NUMBER);
        } else if (DA.equals(tradeService.getDocumentClass()) ||
                DP.equals(tradeService.getDocumentClass()) ||
                OA.equals(tradeService.getDocumentClass()) ||
                DR.equals(tradeService.getDocumentClass())) {
            documentNumber = (String) tradeService.getDetails().get("documentNumber");
        }

        TradeProduct tradeProduct = tradeProductRepository.load(new DocumentNumber(documentNumber));
        Loan loan = new Loan();
        loan.setMainCifNumber(tradeProduct.getMainCifNumber());
        loan.setCifNumber(tradeProduct.getCifNumber());
        loan.setMaturityDate(DateUtil.convertToDateInt(tfsLoan.getMaturityDate()));

        loan.setReportingBranch(Integer.valueOf(tradeService.getProcessingUnitCode()));
        loan.setDocumentNumber(tradeService.getDocumentNumber().toString().replace("-", ""));
        loan.setCurrencyType(detail.getCurrency().toString());
        loan.setInterestRate(detail.getInterestRate().divide(DIVISOR));    //TODO:May have to handle division issues for BigDecimal
        loan.setBranchNumber(Integer.valueOf(tradeService.getCcbdBranchUnitCode()));
        loan.setFacilityId(tfsLoan.getFacility().getFacilityId());
        loan.setLoanTermCode(detail.getLoanTermCode());
        loan.setFacilityReferenceNumber(tfsLoan.getFacility().getFacilityReferenceNumber());
        loan.setFacilityCode(tfsLoan.getFacility().getFacilityType());
        loan.setPaymentCode(tfsLoan.getPaymentCode());
        loan.setOriginalBalance(detail.getAmount());
        loan.setDrawingLimit(detail.getAmount());
        loan.setIntPaymentFrequencyCode(detail.getInterestTermCode());
        loan.setOriginalLoanDate(getValueDate(tradeService.getDetails()));
        loan.setPaymentFrequencyCode(DEFAULT_TERM_CODE);
        loan.setTrustUserId(transactingUser);
        loan.setUnlinkFlag(UNLINK_FLAG);
        loan.setGroupCode(DEFAULT_GROUP_CODE);
        loan.setTransactionStatus(" ");
        loan.setCreditorCode(0L);

        // added as of 31.Jul.2013 - Marv
        String actualAgriAgraTagging = null;
        System.out.println("AGRI AGRA TAGGING PARAMETER:");
        System.out.println(detail.getAgriAgraTagging());
        if (detail.getAgriAgraTagging().equals("AGRI")) {
            actualAgriAgraTagging = "I";
        } else if (detail.getAgriAgraTagging().equals("AGRA")) {
            actualAgriAgraTagging = "A";
        } else if (detail.getAgriAgraTagging().equals("REGULAR")) {
            actualAgriAgraTagging = "R";
        }

        System.out.println("setting agri agra tagging...");
        loan.setAgriAgraTagging(actualAgriAgraTagging);

        if (DocumentSubType2.USANCE.equals(tradeService.getDocumentSubType2())) {
            loan.setLoanTerm(getUALoanTerm(loan.getMaturityDate()));
            loan.setPaymentFrequency(loan.getLoanTerm());
            loan.setPaymentFrequencyCode("D");
            loan.setLoanTermCode("D");
            loan.setIntPaymentFrequencyCode("D");
            loan.setIntPaymentFrequency(loan.getLoanTerm());
            detail.setPaymentTerm(loan.getLoanTerm());
        } else {
            loan.setPaymentFrequency(30);
            detail.setPaymentTerm(30);
            loan.setPaymentFrequencyCode("D");
            loan.setLoanTerm(tfsLoan.getLoanTerm());
            loan.setIntPaymentFrequency(Integer.valueOf(detail.getInterestTerm()));
            loan.setCreditorCode(0L);

        }

        return interfaceLoanService.insertLoan(loan, tfsLoan.getApprovedByCram() != null ? tfsLoan.getApprovedByCram() : false);
    }

    public Loan getLoanBySequenceNumber(Long sequenceNumber){
        return interfaceLoanService.getLoanRequestBySequenceNumber(sequenceNumber);
    }

    public List<String> synchronizeDetails(Long paymentDetailId, String reversalDENumber){
        Payment payment = paymentRepository.getPaymentByPaymentDetailId(paymentDetailId);
        PaymentDetail paymentDetail = payment.getPaymentDetail(paymentDetailId);

        System.out.println(".....OISHIIIIIIIIIIIII....."+ paymentDetail.getSequenceNumber());
        Loan loan = interfaceLoanService.getLoanRequestBySequenceNumber(paymentDetail.getSequenceNumber());
         System.out.println(".....yeeeeeeeeeeeeeeee....."+ loan);
        TradeService tradeService = tradeServiceRepository.load(payment.getTradeServiceId());
        List<String> loanErrors = new ArrayList<String>();

        if("Y".equals(loan.getTransactionStatus())){
            if(loan.isReversal()){
                payment.reverseItemPayment(paymentDetail.getId());
                TradeServiceId reversalTradeServiceId = null;
                if(!StringUtils.isEmpty(reversalDENumber)){
                    reversalTradeServiceId = new TradeServiceId(reversalDENumber);
                }
                eventPublisher.publish(new PaymentItemPaymentReversedEvent(tradeService.getTradeServiceId(),
                        paymentDetail,reversalTradeServiceId));
                earmarkLoanPayment(paymentDetail, tradeService);
            }else{
                payment.payItem(paymentDetail.getId());
                paymentDetail.setPnNumber(loan.getPnNumber());
                eventPublisher.publish(new PaymentItemPaidEvent(payment.getTradeServiceId(),
                        tradeService.getDocumentNumber().toString(), paymentDetail));
            }
        }else if("N".equals(loan.getTransactionStatus())){
            loanErrors = toErrorMessageList(interfaceLoanService.getLoanErrorRecord(paymentDetail.getSequenceNumber()));
            if("U".equals(loan.getUnlinkFlag())){
                paymentDetail.paid();
            }else{
            	earmarkLoanPayment(paymentDetail, tradeService);
                paymentDetail.unPay();
            }
        }else if(StringUtils.isEmpty(loan.getTransactionStatus())){
            paymentDetail.setForInquiry();
        }
        paymentRepository.saveOrUpdate(paymentDetail);
        paymentRepository.merge(payment);

        return loanErrors;
    }

    public long createLoanReversalRequest(Long pnNumber, String transactingUser) throws LoanAlreadyReleasedException, NonExistentLoanException {
        return interfaceLoanService.reverseLoan(pnNumber,transactingUser);
    }

    public List<Map<String,Object>> getLoanErrorRecord(Long sequenceNumber){
        return interfaceLoanService.getLoanErrorRecord(sequenceNumber);
    }
    private int getValueDate(Map<String, Object> details) {
        String date = details.get(NEGOTIATION_VALUE_DATE) != null ? (String) details.get(NEGOTIATION_VALUE_DATE) : (String) details.get(VALUE_DATE);
        try {
            return DateUtil.convertToDateInt(date, "MM/dd/yyyy");
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date: " + date, e);
        }
    }

    private int getUALoanTerm(int loanMaturityDate) {
        try {
            return Days.daysBetween(new DateMidnight(), new DateMidnight(DateUtil.convertFromSibsDateFormat(loanMaturityDate))).getDays();
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date: " + loanMaturityDate, e);
        }
    }

    private List<String> toErrorMessageList(List<Map<String, Object>> sourceList) {
        List<String> result = new ArrayList<String>();
        for (Map<String, Object> row : sourceList) {
            result.add(StringUtils.trim((String)row.get(ERROR_MESSAGE)));
        }
        return result;
    }
    
    public void earmarkLoanPayment(PaymentDetail detail, TradeService tradeService){
    	System.out.println(".....EARMARKING FACILITY.....");
    	if (DocumentClass.LC.equals(DocumentClass.getDocumentClassByName(tradeService.getDocumentClass().toString())) &&
				ServiceType.NEGOTIATION.equals(tradeService.getServiceType()) &&
				(DocumentSubType1.REGULAR.equals(tradeService.getDocumentSubType1()) || 
				DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1()))) {
        	
			Currency currency = null;
					
			if (tradeService.getDetails().get("currency") != null) {
				currency = Currency.getInstance((String) tradeService.getDetails().get("currency"));
            } else if (tradeService.getDetails().get("negotiationCurrency") != null) {
            	currency = Currency.getInstance((String) tradeService.getDetails().get("negotiationCurrency"));
            }
			
			BigDecimal outstandingAmount = new BigDecimal(tradeService.getDetails().get("outstandingBalance").toString());
			
			Boolean isReinstated = Boolean.FALSE;
            if (tradeService.isForReinstatement()) {
                isReinstated = Boolean.TRUE;
            }
            
            
            facilityService.updateAvailmentAmountEarmark(tradeService.getTradeProductNumber().toString(), currency.getCurrencyCode(), detail.getAmountInLcCurrency(), outstandingAmount, isReinstated);
			
        }
    }


}
