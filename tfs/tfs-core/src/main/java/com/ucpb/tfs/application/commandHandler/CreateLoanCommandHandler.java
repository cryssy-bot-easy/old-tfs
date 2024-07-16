package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ucpb.tfs.application.command.CreateLoanCommand;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.TradeProduct;
import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType2;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.domain.sysparams.RefBank;
import com.ucpb.tfs.domain.sysparams.RefBankRepository;
import com.ucpb.tfs.interfaces.domain.Loan;
import com.ucpb.tfs.interfaces.services.FacilityService;
import com.ucpb.tfs.interfaces.services.LoanService;
import com.ucpb.tfs.interfaces.services.RatesService;
import com.ucpb.tfs.utils.DateUtil;
import com.ucpb.tfs.utils.MapUtil;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
	(revision)
SCR/ER Number: 20150622-074 
SCR/ER Description: FXLC Nego - Back Dating not Effected to Generated Loan (Redmine Issue #4037)
[Revised by:] Lymuel Arrome Saul
[Date revised:] 2/18/2016
Program [Revision] Details: The original loan date will be the value date if the value date is not null.
 							The original loan date will be the negotiation date if the value date is null.
 							The original loan date will be the negotiation value date if both the value date
 							and negotiation date is null.
Date deployment: 3/8/2016 
Member Type: JAVA
Project: CORE
Project Name: CreateLoanCommandHandler.java

*/

/**
(revision)
SCR/ER Number: ERF: 20160310-055, 20160421-088 
SCR/ER Description: Incorrect Loan Interest Payment Frequency saved in SIBS Table. (Redmine Issue #3996)
[Revised by:] Jesse James Joson
[Date revised:] 2/18/2016
Program [Revision] Details: The revision will set the correct Interest Payment Frequency and code: it will now get the interest term and not the loan term.
Date deployment: 3/15/2016 
Member Type: JAVA
Project: CORE
Project Name: CreateLoanCommandHandler.java

*/

/**
 * PROLOGUE
 * SCR/ER Description: To include the document number of the EBP in the Loan record on SIBS table.
 *	[Revised by:] Jesse James Joson
 *	Program [Revision] Details: Set the parameter for document number.
 *	Date deployment: 6/16/2016 
	Member Type: JAVA
	Project: CORE
	Project Name: CreateLoanCommandHandler.java
*/

/**
 * PROLOGUE
 * SCR/ER Description:
 *  [Revised by:] Cedrick C. Nungay
 *  [Date revised:] 12/28/2017
 *  Program [Revision] Details: Set group code to 130 if the document class of the transaction is BP.
 *  Date deployment: 6/16/2016 
    Member Type: JAVA
    Project: CORE
    Project Name: CreateLoanCommandHandler.java
*/

/**
 * PROLOGUE
 *  Description: Modified paying by loan, uses LC Negotiation number as PN Number
 *  [Revised by:] Cedrick C. Nungay
 *  [Date revised:] 01/25/2024
*/

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class CreateLoanCommandHandler implements CommandHandler<CreateLoanCommand> {

    private static final String DEFAULT_TERM_CODE = "D";
    private static final int DEFAULT_GROUP_CODE = 180;
    private static final String UNLINK_FLAG = "N";
    private static final BigDecimal DIVISOR = new BigDecimal("100");
    private static final String LOAN_TERM = "loanTerm";
    private static final String LOAN_MATURITY_DATE = "loanMaturityDate";
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
    private static final String NEGOTIATION_DATE = "negotiationDate";
    private static final String CRAM_APPROVAL_FLAG = "withCramApproval";
    private static final String PAYMENT_TERM_CODE = "paymentTermCode";
    private static final String PAYMENT_TERM = "paymentTerm";
    private static final String AGRI_AGRA_TAGGING = "agriAgraTagging";
    private static final int EBP_GROUP_CODE = 130;
    private Date etsDate = null;
    @Autowired
    private TradeServiceRepository tradeServiceRepository;

    @Autowired
    private TradeProductRepository tradeProductRepository;

    @Autowired
    private LoanService loanService;
    
    @Autowired
    private FacilityService facilityService;
    
    @Autowired
    private RatesService ratesService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired(required = false)
    private String silverlakeDate;

    @Autowired
    private RefBankRepository refBankRepository;

    private void setEtsDate(Date etsDate){
    	this.etsDate = etsDate;
    }

    //TODO: refactor this shit. separate and delegate to different methods for UA, IB and TR
    @Override
    public void handle(CreateLoanCommand createLoanCommand) {
        MapUtil map = new MapUtil(createLoanCommand.getParameterMap());
        
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        printParameters(createLoanCommand.getParameterMap());
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        
        String documentNumber="";
        String documentClass="";

        TradeService tradeService = tradeServiceRepository.load(new TradeServiceId(map.getString(TRADE_SERVICE_ID)));
        
        documentClass=(String)tradeService.getDetails().get("documentClass");
        if("LC".equalsIgnoreCase(documentClass)) {
        	documentNumber = (String)tradeService.getDetails().get(LC_NUMBER);

            if (ServiceType.UA_LOAN_SETTLEMENT.equals(tradeService.getServiceType())) {
                documentNumber = (String) tradeService.getDetails().get("documentNumber");
            }
        } else if("DA".equalsIgnoreCase(documentClass) ||
                "DP".equalsIgnoreCase(documentClass) ||
                "OA".equalsIgnoreCase(documentClass) ||
                "DR".equalsIgnoreCase(documentClass)) {
        	documentNumber = (String)tradeService.getDetails().get("documentNumber");
        } else if("BP".equalsIgnoreCase(documentClass) ) {
        	documentNumber = (String)tradeService.getDetails().get("documentNumber");
        }
        
        TradeProduct tradeProduct = tradeProductRepository.load(new DocumentNumber(documentNumber));
        Payment payment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
        //crap code. replace with the code below when the front end has been fixed.
        PaymentDetail detail = payment.getPaymentDetail(PaymentInstrumentType.valueOf(mapModeOfPayment(map.getString(MODE_OF_PAYMENT))));
        //TODO: comment this out for now until we can enforce that the front end will correctly provide the id
//        PaymentDetail detail = payment.getPaymentDetail(map.getAsLong("id"));

        try {
        	Loan loan = new Loan();
        	if (tradeProduct != null){
        		loan = mapParametersToLoan(map,tradeProduct,createLoanCommand.getUserActiveDirectoryId());
        	} else {
        		loan = mapParametersToLoan(map,tradeService,createLoanCommand.getUserActiveDirectoryId());
        	}
            detail.setPnNumber(Long.parseLong(detail.getReferenceNumber().replace("-", "")));
            loan.setPnNumber(detail.getPnNumber());

        	//override setting for changed main cif number
        	if(tradeProduct != null){
	        	if((tradeService.getMainCifNumber() != null) && (tradeProduct.getMainCifNumber() != null)){
		        	if(!tradeService.getMainCifNumber().equals(tradeProduct.getMainCifNumber())){
		        		loan.setMainCifNumber(tradeService.getMainCifNumber());
		        	}
	        	}
        	}

            loan.setReportingBranch(Integer.valueOf(tradeService.getProcessingUnitCode()));
			loan.setDocumentNumber(documentNumber.replace("-", ""));
            loan.setCurrencyType(detail.getCurrency().toString());
            BigDecimal interestRate = detail.getInterestRate() != null ? detail.getInterestRate().divide(DIVISOR) : BigDecimal.ZERO;   //TODO:May have to handle division issues for BigDecimal
            loan.setInterestRate(interestRate);
            loan.setBranchNumber(Integer.valueOf(tradeService.getCcbdBranchUnitCode()));
            loan.setFacilityId(map.getAsInteger(FACILITY_ID));
            loan.setFacilityReferenceNumber(map.getString(FACILITY_REFERENCE_NUMBER));
            loan.setFacilityCode(map.getString(FACILITY_TYPE));

            // added as of 27.Jul.2013 - Marv
            String actualAgriAgraTagging = null;
            System.out.println("AGRI AGRA TAGGING PARAMETER:");
            System.out.println(map.getString(AGRI_AGRA_TAGGING));
            if (map.getString(AGRI_AGRA_TAGGING).equals("AGRI")) {
                actualAgriAgraTagging = "I";
            } else if (map.getString(AGRI_AGRA_TAGGING).equals("AGRA")) {
                actualAgriAgraTagging = "A";
            } else if (map.getString(AGRI_AGRA_TAGGING).equals("REGULAR")) {
                actualAgriAgraTagging = "R";
            }

            System.out.println("setting agri agra tagging...");
            loan.setAgriAgraTagging(actualAgriAgraTagging);

            if(!"UA_LOAN".equals(map.getString(MODE_OF_PAYMENT))){
	            //loan.setPaymentCode(map.getAsInteger(PAYMENT_CODE));
	            if (map.getAsInteger(PAYMENT_CODE) != null) {
	                loan.setPaymentCode(map.getAsInteger(PAYMENT_CODE));
	            } else {
	                loan.setPaymentCode(map.getAsInteger("loanPaymentCode"));
	            }
            }

            loan.setOriginalBalance(detail.getAmount());
            loan.setDrawingLimit(detail.getAmount());
            loan.setOriginalLoanDate(getValueDate(tradeService.getDetails()));
            loan.setPaymentFrequencyCode(DEFAULT_TERM_CODE);
            loan.setTrustUserId(createLoanCommand.getUserActiveDirectoryId());
            loan.setUnlinkFlag(UNLINK_FLAG);
            if (DocumentClass.BP.equals(DocumentClass.valueOf(documentClass))) {
                System.out.println("Document class is BP...");
                loan.setGroupCode(EBP_GROUP_CODE);
            } else {
                System.out.println("Document class is not BP...");
                loan.setGroupCode(DEFAULT_GROUP_CODE);
            }
            loan.setTransactionStatus(" ");

            // Start ERF: 20160310-055, 20160421-088  - Added this part
            // This will set the right interest payment frequency & code
            String intPaymentCode = DEFAULT_TERM_CODE;
            int intPaymentFreq=loan.getLoanTerm();         
            try {
            	System.out.println("HERE...");
            	if (tradeService.getDetails().get("interestTermCode") != null
                        && tradeService.getDetails().get("interestTermCode").toString()!=null
                        && !tradeService.getDetails().get("interestTermCode").toString().isEmpty()
            			&& tradeService.getDetails().get("interestTermCode").toString()!="") {
            		System.out.println("Interest Payment Code: " + tradeService.getDetails().get("interestTermCode").toString());
            		intPaymentCode = tradeService.getDetails().get("interestTermCode").toString();
            		intPaymentFreq = Integer.parseInt(tradeService.getDetails().get("interestTerm").toString());
            	}
            } catch (Exception e) {
            	e.printStackTrace();
            }
            // End: 20160310-055, 20160421-088 


            if(DocumentSubType2.USANCE.equals(tradeService.getDocumentSubType2())){
                System.out.println("loan maturity date : " + loan.getMaturityDate());
                setEtsDate(map.getAsDate("etsDate"));
                loan.setLoanTerm(getUALoanTerm(loan.getMaturityDate()));
                // Start ERF: 20160310-055, 20160421-088  - Added this part
                // This will set the right interest payment frequency & code                
                if (intPaymentFreq==0) {
                	intPaymentFreq=loan.getLoanTerm();  
                }
                // End: 20160310-055, 20160421-088 

                loan.setPaymentFrequency(loan.getLoanTerm());
                loan.setPaymentFrequencyCode("D");
                loan.setLoanTermCode("D");
                loan.setIntPaymentFrequencyCode(intPaymentCode);
                loan.setIntPaymentFrequency(intPaymentFreq);
                detail.setPaymentTerm(loan.getLoanTerm());
                if(DocumentType.FOREIGN.equals(tradeService.getDocumentType())){
                	loan.setCreditorCode(getCreditorCode(tradeService));
                } else {
                	loan.setCreditorCode(0L);
                }
                
            }else{
                loan.setLoanTermCode(detail.getLoanTermCode());
                loan.setPaymentFrequency(map.getAsInteger(PAYMENT_TERM));
                detail.setPaymentTerm(map.getAsInteger(PAYMENT_TERM));
                loan.setPaymentFrequencyCode(map.getString(PAYMENT_TERM_CODE));
                loan.setIntPaymentFrequencyCode(detail.getInterestTermCode());
                MapUtil loanDetails = new MapUtil(convertToMap(map.getString("setupString")));
                loan.setLoanTerm(loanDetails.getAsInteger(LOAN_TERM));
                loan.setIntPaymentFrequency(Integer.valueOf(detail.getInterestTerm()));
                loan.setCreditorCode(0L);

            }
            detail.setFacilityId(map.getAsInteger(FACILITY_ID));
            detail.setFacilityType(map.getString(FACILITY_TYPE));
            detail.setFacilityReferenceNumber(map.getString(FACILITY_REFERENCE_NUMBER));

            //detail.setPaymentCode(map.getAsInteger(PAYMENT_CODE));
            if (map.getAsInteger(PAYMENT_CODE) != null) {
                detail.setPaymentCode(map.getAsInteger(PAYMENT_CODE));
            } else {
                detail.setPaymentCode(map.getAsInteger("loanPaymentCode"));
            }

            
			if ("LC".equalsIgnoreCase(documentClass) &&
					ServiceType.NEGOTIATION.equals(tradeService.getServiceType()) &&
					(DocumentSubType1.REGULAR.equals(tradeService.getDocumentSubType1()) || 
					DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1()))) {
            	
				Currency currency = null;
						
				if (tradeService.getDetails().get("currency") != null) {
					currency = Currency.getInstance((String) tradeService.getDetails().get("currency"));
                } else if (tradeService.getDetails().get("negotiationCurrency") != null) {
                	currency = Currency.getInstance((String) tradeService.getDetails().get("negotiationCurrency"));
                }
				
				BigDecimal amount = new BigDecimal(tradeService.getDetails().get("negotiationAmount").toString());
				BigDecimal outstandingAmount = new BigDecimal(tradeService.getDetails().get("outstandingBalance").toString());
				
				Boolean isReinstated = Boolean.FALSE;
                if (tradeService.isForReinstatement()) {
                    isReinstated = Boolean.TRUE;
                }
                
                
                facilityService.updateAvailmentAmountUnearmark(tradeService.getTradeProductNumber().toString(), currency.getCurrencyCode(), detail.getAmountInLcCurrency(), outstandingAmount, isReinstated, amount);
				
            }
            
            printLoanParameters(loan, map);
            detail.setForInquiry();
            Boolean cramOverride = map.getAsBoolean(CRAM_APPROVAL_FLAG);
            detail.setWithCramApproval(cramOverride);
            detail.setSequenceNumber(loanService.insertLoan(loan, cramOverride != null ? cramOverride : false ));
            detail.setAgriAgraTagging(map.getString(AGRI_AGRA_TAGGING));
            payment.put(detail);
            paymentRepository.saveOrUpdate(payment);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date",e);
        } catch (Exception e){
            System.out.println("**************");
            e.printStackTrace();
            System.out.println("**************");
            throw new RuntimeException(e);
        }
    }


    //TODO: Map from payment detail
    private Loan mapParametersToLoan(MapUtil param,TradeProduct product, String userId) throws ParseException {
        System.out.println("setupString " + (String)param.get("setupString"));
        MapUtil loanDetails = new MapUtil(convertToMap((String)param.get("setupString")));
        Loan loan = new Loan();

        loan.setMainCifNumber(product.getMainCifNumber());
        loan.setCifNumber(product.getCifNumber());
        loan.setMaturityDate(DateUtil.convertToDateInt(loanDetails.getString(LOAN_MATURITY_DATE)));
        return loan;
    }
    
    //TODO: Map from payment detail
    private Loan mapParametersToLoan(MapUtil param,TradeService service, String userId) throws ParseException {
    	System.out.println("setupString " + (String)param.get("setupString"));
    	MapUtil loanDetails = new MapUtil(convertToMap((String)param.get("setupString")));
    	Loan loan = new Loan();
    	
    	loan.setMainCifNumber(service.getMainCifNumber());
    	loan.setCifNumber(service.getCifNumber());
    	loan.setMaturityDate(DateUtil.convertToDateInt(loanDetails.getString(LOAN_MATURITY_DATE)));
    	return loan;
    }

    private Map<String,Object> convertToMap(String source){
        System.out.println("source " + source);
        Map<String,Object> result = new HashMap<String,Object>();
        for(String row : source.split("&")){
            String[] entry = row.split("=");
            result.put(entry[0],entry[1]);
        }
        return result;
        
    }

    private int getUALoanTerm(int loanMaturityDate) throws ParseException {
        return Days.daysBetween(new DateMidnight(DateUtil.convertFromSibsDateFormat(DateUtil.convertToDateInt(etsDate))),new DateMidnight(DateUtil.convertFromSibsDateFormat(loanMaturityDate))).getDays();
    }

    private int getValueDate(Map<String,Object> details) throws ParseException {
        String date = details.get(VALUE_DATE) != null ? (String)details.get(VALUE_DATE) : (details.get(NEGOTIATION_DATE) != null ? (String)details.get(NEGOTIATION_DATE) : (String)details.get(NEGOTIATION_VALUE_DATE)); //ERF#: 20150622-074
//        String date = details.get(NEGOTIATION_VALUE_DATE) != null ? (String)details.get(NEGOTIATION_VALUE_DATE) : (String)details.get(VALUE_DATE);
        return DateUtil.convertToDateInt(date,"MM/dd/yyyy");
    }

    private int getSilverlakeDate() throws ParseException {
        if(silverlakeDate != null){
            return DateUtil.convertToDateInt(silverlakeDate,"MM/dd/yyyy");
        }
        return DateUtil.convertToDateInt(new Date());
    }

    private String mapModeOfPayment(String modeOfPayment){
        String result = modeOfPayment;
        if("DTR_LOAN".equalsIgnoreCase(modeOfPayment)){
            result = "TR_LOAN";
        }else if("DUA_LOAN".equalsIgnoreCase(modeOfPayment)){
            result =  "UA_LOAN";
        }
        return result;
    }

    private Long getCreditorCode(TradeService tradeService){
        String swiftCode = (String)tradeService.getDetails().get("reimbursingBank");
        RefBank reimbursingBank = refBankRepository.getBank(StringUtils.left(swiftCode,8), StringUtils.right(swiftCode,3));
        if(reimbursingBank == null){
//            throw new RuntimeException("Reimbursing bank" + swiftCode + " does not exist!");
        	return 0L;
        }
        return reimbursingBank.getCbCreditorCode();
    }

 // temporary prints parameters
    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("create loan command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }
    
    private void printLoanParameters(Loan loan, MapUtil map) {
    	System.out.println("--------------------" + map.getString(MODE_OF_PAYMENT) + "PARAMETERS--------------------");
    	System.out.println("AANO - mainCifNumber - " + loan.getMainCifNumber());
    	System.out.println("FCODE - facilityCode - " + loan.getFacilityCode());
    	System.out.println("FSEQ - facilityId - " + loan.getFacilityId());
    	System.out.println("TRSEQ - transactionSequenceNumber - " + loan.getTransactionSequenceNumber());
    	System.out.println("ACCTNO - pnNumber - " + loan.getPnNumber());
    	System.out.println("BR# - branchNumber - " + loan.getBranchNumber());
    	System.out.println("RBR# - reportingBranch - " + loan.getReportingBranch());
    	System.out.println("CURTYP - currencyType - " + loan.getCurrencyType());
    	System.out.println("CIFNO - cifNumber - " + loan.getCifNumber());
    	System.out.println("TERM - loanTerm - " + loan.getLoanTerm());
    	System.out.println("TMCODE - loanTermCode - " + loan.getLoanTermCode());
    	System.out.println("ORGAMT - originalBalance - " + loan.getOriginalBalance());
    	System.out.println("ORGDT6 - originalLoanDate - " + loan.getOriginalLoanDate());
    	System.out.println("RATE - interestRate - " + loan.getInterestRate());
    	System.out.println("DRLIMT - originalBalance - " + loan.getOriginalBalance());
    	System.out.println("MATDT6 - maturityDate - " + loan.getMaturityDate());
    	System.out.println("FREQ - paymentFrequency - " + loan.getPaymentFrequency());
    	System.out.println("FRCODE - paymentFrequencyCode - " + loan.getPaymentFrequencyCode());
    	System.out.println("IPFREQ - intPaymentFrequency - " + loan.getIntPaymentFrequency());
    	System.out.println("IPCODE - intPaymentFrequencyCode - " + loan.getIntPaymentFrequencyCode());
    	System.out.println("GROUP - groupCode - " + loan.getGroupCode());
    	System.out.println("TNUMBR - documentNumber - " + loan.getDocumentNumber());
    	System.out.println("TEXP6 - orderExpiryDate - " + loan.getOrderExpiryDate());
    	System.out.println("TRUNLINK - unlinkFlag - " + loan.getUnlinkFlag());
    	System.out.println("TRUSERID - trustUserId - " + loan.getTrustUserId());
    	System.out.println("CRDTCD - creditorCode - " + loan.getCreditorCode());
    	System.out.println("PMTCOD - paymentCode - " + loan.getPaymentCode());
    	System.out.println("REQSTS - overrideFlag - " + map.getAsBoolean(CRAM_APPROVAL_FLAG));

        System.out.println("RS4FLG - agriAgraTagging - " + loan.getAgriAgraTagging());
        System.out.println("DOCFLG - docStampCalculationFlag - " + loan.getDocStampCalculationFlag());
    	System.out.println("-------------------------------------------------------");
    }

}
