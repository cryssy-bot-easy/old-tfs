package com.ucpb.tfs.application.batch;

import com.ucpb.tfs.application.service.AccountingService;
import com.ucpb.tfs.batch.job.SpringJob;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.domain.product.enums.LCType;
import com.ucpb.tfs.domain.reference.GltsSequenceRepository;
import com.ucpb.tfs.domain.service.enumTypes.*;
import com.ucpb.tfs.interfaces.services.impl.FacilityServiceImpl;
import com.ucpb.tfs.interfaces.services.impl.RatesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * TradeProductExpireJob class to set
 */
 
  /**
 	(revision)
	SCR/ER Number: ER# 20151009-040 & ER# 20151016-060
	SCR/ER Description: Effective date of cancellation entries = expiry date, which is wrong. Failed Status upon execution due to Null URR field of old domestic LC's.
	[Revised by:] Jesse James Joson
	[Date revised:] 10/21/2015
	Program [Revision] Details: Set effective date = batch run date + 1. Filter URR field, if null set to value one(1) else proceed with URR Value. Used value one(1) to retain its original value and avoid error.
	PROJECT: CORE
	MEMBER TYPE  : JAVA


 	(revision)
	SCR/ER Number: 
	SCR/ER Description: 172 Linux Migration 
	[Revised by:] Raymund Mallonga
	[Date revised:] 10/30/2019
	Program [Revision] Details: Change CAST as timestamp to TO_DATE function since date pattern in the string being passed has MM-DD-YYYY pattern.
	PROJECT: CORE
	MEMBER TYPE  : JAVA


 */
 
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TradeProductExpireJob<T> implements SpringJob {


    private TradeProductRepository tradeProductRepository;

    private AccountingService accountingService;

    private GltsSequenceRepository gltsSequenceRepository;

    private RatesServiceImpl ratesService;

    private DataSource tfsDataSource;

    @Autowired
    private FacilityServiceImpl facilityService;

//    private static String SQL = "select DOCUMENTNUMBER FROM TRADEPRODUCT WHERE DOCUMENTNUMBER IN (SELECT DOCUMENTNUMBER FROM LETTEROFCREDIT WHERE :ts > EXPIRYDATE) AND STATUS <> 'EXPIRED'";
   // private static String SQL ="select DOCUMENTNUMBER FROM TRADEPRODUCT WHERE DOCUMENTNUMBER IN (SELECT DOCUMENTNUMBER FROM LETTEROFCREDIT WHERE TYPE <> 'CASH' AND :ls <= EXPIRYDATE AND :ts >= EXPIRYDATE) AND STATUS IN ('OPEN','REINSTATED', 'EXPIRED')";
private static String SQL ="select DOCUMENTNUMBER FROM TRADEPRODUCT WHERE DOCUMENTNUMBER IN (SELECT DOCUMENTNUMBER FROM LETTEROFCREDIT WHERE TYPE <> 'CASH' AND :ts = EXPIRYDATE) AND STATUS IN ('OPEN','REINSTATED')";
 
   
   //        "\n" +
//        "select DOCUMENTNUMBER FROM TRADEPRODUCT WHERE DOCUMENTNUMBER IN (SELECT DOCUMENTNUMBER FROM LETTEROFCREDIT WHERE :ts > EXPIRYDATE) AND STATUS <> 'EXPIRED' \n" +
//        "UNION \n" +
//        "select DOCUMENTNUMBER FROM TRADEPRODUCT WHERE DOCUMENTNUMBER IN (SELECT DOCUMENTNUMBER FROM DOCUMENTAGAINSTACCEPTANCE WHERE :ts >  MATURITYDATE) AND STATUS <> 'EXPIRED' \n" +
//        "UNION \n" +
//        "select DOCUMENTNUMBER FROM TRADEPRODUCT WHERE DOCUMENTNUMBER IN (SELECT DOCUMENTNUMBER FROM DOCUMENTAGAINSTPAYMENT WHERE :ts > MATURITYDATE) AND STATUS <> 'EXPIRED' ";

    public TradeProductExpireJob(TradeProductRepository tradeProductRepository, AccountingService accountingService, GltsSequenceRepository gltsSequenceRepository, DataSource tfsDataSource) {
        this.tradeProductRepository = tradeProductRepository;
        this.accountingService = accountingService;
        this.gltsSequenceRepository = gltsSequenceRepository;
        this.tfsDataSource = tfsDataSource;
    }



    @Override
    @Transactional
    public void execute() {
        System.out.println("TradeProductExpireJob execute none");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String reportDate = simpleDateFormat.format(new Date());
        Date lastExpiredDate = tradeProductRepository.getLastExpiredDate();
        System.out.println("lastExpiredDate:"+lastExpiredDate);
        //List<String> documentNumberExpiryList = tradeProductRepository.getTradeProductToBeExpired(SQL, reportDate, lastExpiredDate);//				
		List<String> documentNumberExpiryList = tradeProductRepository.getTradeProductToBeExpired2("SELECT LC.DOCUMENTNUMBER FROM LETTEROFCREDIT LC JOIN TRADEPRODUCT TP ON LC.DOCUMENTNUMBER = TP.DOCUMENTNUMBER WHERE LC.TYPE <> 'CASH' AND TP.STATUS IN ('OPEN','REINSTATED') AND LC.EXPIRYDATE <= CAST('"+reportDate+"' AS TIMESTAMP)");
		System.out.println("documentNumberExpiryList:"+documentNumberExpiryList);
        for (String documentNumberStr : documentNumberExpiryList){
            try {
                System.out.println("DocumentNumber to be expired:"+documentNumberStr);
                //fire cancellation for this

//                DocumentNumber docNum = new DocumentNumber(documentNumberStr);
//                Map lcMap =tradeProductRepository.loadToMap(docNum);


//                try {
//                    System.out.println(tfsDataSource);
//                    System.out.println(tfsDataSource.getConnection().toString());
////                String tfsUpdate2 = "UPDATE GLTS_SEQUENCE SET SEQUENCE = (SEQUENCE + 1)";
////                    String tfsUpdate = "UPDATE TRADEPRODUCT SET STATUS = 'EXPIRED' WHERE DOCUMENTNUMBER='" +documentNumberStr+"'";
//                    String tfsUpdate = "UPDATE TRADEPRODUCT SET (STATUS)= ('EXPIRED') WHERE DOCUMENTNUMBER='" +documentNumberStr+"'";
//
//
//                    Connection tfsConn = tfsDataSource.getConnection();
//                    PreparedStatement tfsPs = tfsConn.prepareStatement(tfsUpdate);
//                    tfsPs.executeUpdate();
//                    tfsConn.commit();
//
////                PreparedStatement tfsPs1 = tfsConn.prepareStatement(tfsUpdate2);
////                tfsPs1.executeUpdate();
////                tfsConn.commit();
//                } catch (Exception e){
//                    e.printStackTrace();
//                }

                Map lcMap =tradeProductRepository.getLC(documentNumberStr);
                System.out.println("lcMap:"+lcMap);
                System.out.println("currency:"+lcMap.get("currency"));

                System.out.println("lcMap:"+lcMap);
                System.out.println("documentType:"+lcMap.get("documentType"));
                System.out.println("type:"+lcMap.get("type"));
                System.out.println("tenor:"+lcMap.get("tenor"));
                System.out.println("outstandingBalance:"+lcMap.get("outstandingBalance"));
				
				
                System.out.println("Declaring URR");
                BigDecimal urr;// =  new BigDecimal(lcMap.get("urr").toString());   
                System.out.println("URR conditioning");
                if(lcMap.get("urr").toString() == null || lcMap.get("urr") == ""){
                	System.out.println("URR is null it will turn it to One(1)");
                	urr = new BigDecimal("1");
                } else {
                	System.out.println("URR is not null");
                	urr = new BigDecimal(lcMap.get("urr").toString());
                }
                System.out.println("URR = " + urr);
                
                String lcCurrency = lcMap.get("currency").toString();
                BigDecimal outstandingLCAmount= new BigDecimal(lcMap.get("outstandingBalance").toString());
				String cashFlag = lcMap.get("cashFlag").toString();           
                BigDecimal cashAmount = new BigDecimal(lcMap.get("cashAmount").toString());	
                BigDecimal totalNegotiatedCashAmount = new BigDecimal(lcMap.get("totalNegotiatedCashAmount").toString());               	 
                BigDecimal currentAmount = new BigDecimal(lcMap.get("currentAmount").toString());
                BigDecimal totalnegoAmount = new BigDecimal(lcMap.get("totalnegoAmount").toString());
             
				
				//addition
                BigDecimal amount = outstandingLCAmount;
                if(cashFlag.equalsIgnoreCase("1") ){    
                	
                	try{
                	cashAmount = cashAmount.subtract(totalNegotiatedCashAmount); 
                	amount = outstandingLCAmount.subtract(cashAmount);
 
                	}catch(Exception e){
                		System.out.println("cashAmount: " + cashAmount);
                		System.out.println("totalNegotiatedCashAmount: " + totalNegotiatedCashAmount);
                		System.out.println("outstandingBalance: " + outstandingLCAmount);
                		amount = BigDecimal.ZERO;
                	}
                }      	
                else{
                 amount = outstandingLCAmount;
                }
                cashAmount = amount;
                //addition
				
                String lcType = lcMap.get("type").toString();
                String lcTenor = lcMap.get("tenor").toString();
                String expiryDate = lcMap.get("expiryDate").toString();
                DocumentClass documentClass = DocumentClass.LC;

                DocumentType documentType = null;
                if(DocumentType.DOMESTIC.toString().equalsIgnoreCase(lcMap.get("documentType").toString())){
                    documentType = DocumentType.DOMESTIC;
                } else {
                    documentType = DocumentType.FOREIGN;
                }
                DocumentSubType1 documentSubType1 = null;
                DocumentSubType2 documentSubType2 = null;


                if(lcType.equalsIgnoreCase("CASH")){
                    documentSubType1 = DocumentSubType1.CASH;
                    documentSubType2 = DocumentSubType2.SIGHT;
                } else if(lcType.equalsIgnoreCase("STANDBY")){
                    documentSubType1 = DocumentSubType1.STANDBY;
                    documentSubType2 = DocumentSubType2.SIGHT;
                } else if(lcType.equalsIgnoreCase("REGULAR")){
                    documentSubType1 = DocumentSubType1.REGULAR;
                    if(lcTenor.equalsIgnoreCase("SIGHT")){
                        documentSubType2 = DocumentSubType2.SIGHT;
                    } else if(lcTenor.equalsIgnoreCase("USANCE")) {
                        documentSubType2 = DocumentSubType2.USANCE;
                    }
                }

                ServiceType serviceType = ServiceType.CANCELLATION;


                Map<String,Object> details = new HashMap<String, Object>();
                details.put("documentNumberStr",documentNumberStr);
                details.put("lcCurrency",lcCurrency);
                details.put("serviceType",serviceType);
                details.put("outstandingLCAmount",outstandingLCAmount);
                details.put("standbyTagging",lcMap.get("standbyTagging"));
				details.put("cashFlag", cashFlag);
                details.put("cashAmount", cashAmount);
           	 	details.put("totalNegotiatedCashAmount", totalNegotiatedCashAmount);
           		details.put("currentAmount", currentAmount);
           		details.put("totalnegoAmount", totalnegoAmount);
				details.put("urr", urr);
				details.put("expiryDate",expiryDate);
				details.put("effectiveDate", reportDate);
                Map discrepancy = (Map) lcMap.get("negotiationDiscrepancies");
                if(discrepancy!=null && discrepancy.size()>0){
                    details.put("withDiscrepancy","Y");
                } else {
                    details.put("withDiscrepancy","N");
                }


                System.out.println("ratesService.getAngolConversionRate():"+ratesService.getAngolConversionRate(lcCurrency,"USD",2));

                //TODO INSERT RATE HERE
                if(lcCurrency.equalsIgnoreCase("PHP")){
                    System.out.println("LC PHP");
                    ratesService.getUrrConversionRateToday();

                } else if(lcCurrency.equalsIgnoreCase("USD")){
                    System.out.println("LC USD");
                    details.put("urr",ratesService.getUrrConversionRateToday());
                } else {
                    System.out.println("LC THIRD");


                    details.put("urr",ratesService.getUrrConversionRateToday());
                    details.put("thirdToUSD",ratesService.getAngolConversionRate(lcCurrency,"USD",2));
                }

                System.out.println("details:"+details);
                String gltsNumber = gltsSequenceRepository.getGltsSequence();
                System.out.println("gltsNumber:"+gltsNumber);
                accountingService.generateActualEntriesForTradeProductCancellation(details, gltsNumber, documentClass, documentType, documentSubType1, documentSubType2, serviceType);
                gltsSequenceRepository.incrementGltsSequence();

                if (lcType.equalsIgnoreCase(LCType.REGULAR.toString()) || lcType.equalsIgnoreCase(LCType.STANDBY.toString())) {
                    // Un-earmark
                    System.out.println("\n$$$ FACILITY UN-EARMARKING (Expired LCs) :::::::::\n");
                    facilityService.unearmarkAvailment(documentNumberStr);
                }

//
//                try {
//                    String tfsUpdate = "UPDATE TRADEPRODUCT SET STATUS = 'EXPIRED' WHERE DOCUMENTNUMBER='" +documentNumberStr+"'";
//
//                    Connection tfsConn = tfsDataSource.getConnection();
//                    PreparedStatement tfsPs = tfsConn.prepareStatement(tfsUpdate);
//                    tfsPs.executeUpdate();
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//                tradeProductRepository.expireDocNum(documentNumberStr);

/*
                LetterOfCredit lc = (LetterOfCredit) tradeProductRepository.load(docNum);
                Currency lcCurrency =  lc.getCurrency();
                System.out.println("lc.getCurrency():"+lc.getCurrency());
                System.out.println("lc.getUrr():"+lc.getUrr());
                System.out.println("lc.getSpecialRateThirdToUsd():"+lc.getSpecialRateThirdToUsd());
                System.out.println("lc.getPassOnRateThirdToUsd():"+lc.getPassOnRateThirdToUsd());
                lc.updateStatus(TradeProductStatus.EXPIRED);
                lc.updateLastModifiedDate();

                LCDocumentType lcDocumentType = lc.getDocumentType(); //DOCUMENT TYPE FX or DM
                LCType lcType = lc.getType(); //REGULAR("25"), CASH("0"), STANDBY("20"), DEFFERED("10"), REVOLVING("0");
                LCTenor lcTenor = lc.getTenor();  //SIGHT("Sight"), USANCE("Usance"); //, OTHER("Other");
                BigDecimal outstandingLCAmount = lc.getOutstandingBalance();

                DocumentClass documentClass = DocumentClass.LC;
                DocumentType documentType = null;
                DocumentSubType1 documentSubType1 = null;
                DocumentSubType2 documentSubType2 = null;

                if(LCDocumentType.DOMESTIC.equals(lcDocumentType)){
                    documentType = DocumentType.DOMESTIC;
                } else if(LCDocumentType.FOREIGN.equals(lcDocumentType)) {
                    documentType = DocumentType.FOREIGN;
                }

                if(lcType.equals(LCType.CASH)){
                    documentSubType1 = DocumentSubType1.CASH;
                    documentSubType2 = DocumentSubType2.SIGHT;
                } else if(lcType.equals(LCType.STANDBY)){
                    documentSubType1 = DocumentSubType1.STANDBY;
                    documentSubType2 = DocumentSubType2.SIGHT;
                } else if(lcType.equals(LCType.REGULAR)){
                    documentSubType1 = DocumentSubType1.REGULAR;
                    if(lcTenor.equals(LCTenor.SIGHT)){
                        documentSubType2 = DocumentSubType2.SIGHT;
                    } else if(lcTenor.equals(LCTenor.USANCE)) {
                        documentSubType2 = DocumentSubType2.USANCE;
                    }
                }



                ServiceType serviceType = ServiceType.CANCELLATION;


                Map<String,Object> details = new HashMap<String, Object>();
                details.put("documentNumberStr",documentNumberStr);
                details.put("lcCurrency",lcCurrency.getCurrencyCode());
                details.put("serviceType",serviceType);
                details.put("outstandingLCAmount",outstandingLCAmount);
                details.put("standbyTagging",lc.getStandbyTagging());
                if(lc.getNegotiationDiscrepancies()!=null &&lc.getNegotiationDiscrepancies().size()>0){
                    details.put("withDiscrepancy","Y");
                } else {
                    details.put("withDiscrepancy","N");
                }


                System.out.println("ratesService.getAngolConversionRate():"+ratesService.getAngolConversionRate(lcCurrency.getCurrencyCode(),"USD",2));

                //TODO INSERT RATE HERE
                if(lcCurrency.getCurrencyCode().equalsIgnoreCase("PHP")){
                    System.out.println("LC PHP");
                    ratesService.getUrrConversionRateToday();

                } else if(lcCurrency.getCurrencyCode().equalsIgnoreCase("USD")){
                    System.out.println("LC USD");
                    details.put("urr",ratesService.getUrrConversionRateToday());
                } else {
                    System.out.println("LC THIRD");


                    details.put("urr",ratesService.getUrrConversionRateToday());
                    details.put("thirdToUSD",ratesService.getAngolConversionRate(lcCurrency.getCurrencyCode(),"USD",2));
                }

                System.out.println("details:"+details);
                String gltsNumber = gltsSequenceRepository.getGltsSequence();
                System.out.println("gltsNumber:"+gltsNumber);
                accountingService.generateActualEntriesForTradeProductCancellation(details, gltsNumber, documentClass, documentType, documentSubType1, documentSubType2, serviceType);
                gltsSequenceRepository.incrementGltsSequence();

                if(lcType.equals(LCType.REGULAR) || lcType.equals(LCType.STANDBY)){
                    // Un-earmark
                    System.out.println("\n$$$ FACILITY UN-EARMARKING (Expired LCs) :::::::::\n");
                    facilityService.unearmarkAvailment(documentNumberStr);
                }
                tradeProductRepository.persist(lc);

                */


            }catch(Exception e){
               e.printStackTrace();
            }

        }
    }

    @Transactional
    public void execute(String reportDate) {
    	String reportDateq = reportDate;
    	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	
         System.out.println("TradeProductExpireJob execute reportDate");
         System.out.println("reportDate:"+reportDate);
        Date lastExpiredDate = tradeProductRepository.getLastExpiredDate();       
        //reportDate = lastExpiredDate.toString();
         System.out.println("lastExpiredDate:"+lastExpiredDate);
         //List<String> documentNumberExpiryList = tradeProductRepository.getTradeProductToBeExpired(SQL, reportDate, lastExpiredDate);
        List<String> documentNumberExpiryList = tradeProductRepository.getTradeProductToBeExpired2("SELECT LC.DOCUMENTNUMBER FROM LETTEROFCREDIT LC JOIN TRADEPRODUCT TP ON LC.DOCUMENTNUMBER = TP.DOCUMENTNUMBER WHERE LC.TYPE <> 'CASH' AND TP.STATUS IN ('OPEN','REINSTATED') AND LC.EXPIRYDATE <= TO_DATE('"+reportDate+"','MM-DD-YYYY')");
         System.out.println("documentNumberExpiryList:"+documentNumberExpiryList);
         for (String documentNumberStr : documentNumberExpiryList){
            System.out.println("DocumentNumber to be expired:"+documentNumberStr);
      
            //fire cancellation for this


/*
            try {
                System.out.println(tfsDataSource.getConnection());
                System.out.println(tfsDataSource);
//                String tfsUpdate2 = "UPDATE GLTS_SEQUENCE SET SEQUENCE = (SEQUENCE + 1)";
                String tfsUpdate = "UPDATE TRADEPRODUCT SET (STATUS)= ('EXPIRED') WHERE DOCUMENTNUMBER='" +documentNumberStr+"'";

                Connection tfsConn = tfsDataSource.getConnection();
                PreparedStatement tfsPs = tfsConn.prepareStatement(tfsUpdate);
                tfsPs.executeUpdate();
                tfsConn.commit();

//                PreparedStatement tfsPs1 = tfsConn.prepareStatement(tfsUpdate2);
//                tfsPs1.executeUpdate();
//                tfsConn.commit();
            } catch (Exception e){
                e.printStackTrace();
            }
*/

            DocumentNumber docNum = new DocumentNumber(documentNumberStr);
//            Map lcMap =tradeProductRepository.loadToMap(docNum);
            Map lcMap =tradeProductRepository.getLC(documentNumberStr);
            System.out.println("lcMap:"+lcMap);
            System.out.println("documentType:"+lcMap.get("documentType"));
            System.out.println("type:"+lcMap.get("type"));
            System.out.println("tenor:"+lcMap.get("tenor"));
            System.out.println("outstandingBalance:"+lcMap.get("outstandingBalance"));
			
            System.out.println("Declaring URR");
            BigDecimal urr;// =  new BigDecimal(lcMap.get("urr").toString());   
            System.out.println("URR conditioning******");
            if(lcMap.get("urr") == null || lcMap.get("urr") == ""){
            	System.out.println("URR is null it will turn it to One(1)");
            	urr = new BigDecimal("1");
            } else {
            	System.out.println("URR is not null");
            	urr = new BigDecimal(lcMap.get("urr").toString());
            }
            System.out.println("URR = " + urr);
            
            String lcCurrency = lcMap.get("currency").toString();
            BigDecimal outstandingLCAmount= new BigDecimal(lcMap.get("outstandingBalance").toString());
			String cashFlag = lcMap.get("cashFlag").toString();       
            BigDecimal cashAmount = new BigDecimal(lcMap.get("cashAmount").toString());	
            BigDecimal totalNegotiatedCashAmount = new BigDecimal(lcMap.get("totalNegotiatedCashAmount").toString());
            BigDecimal currentAmount = new BigDecimal(lcMap.get("currentAmount").toString());
            BigDecimal totalnegoAmount = new BigDecimal(lcMap.get("totalnegoAmount").toString());
             
			
            String lcType = lcMap.get("type").toString();
            String lcTenor = lcMap.get("tenor").toString();
			String expiryDate = lcMap.get("expiryDate").toString();
            DocumentClass documentClass = DocumentClass.LC;

			 //addition
            BigDecimal amount = outstandingLCAmount;
            if(cashFlag.equalsIgnoreCase("1") ){    
            	
            	try{
            	cashAmount = cashAmount.subtract(totalNegotiatedCashAmount); 
            	amount = outstandingLCAmount.subtract(cashAmount);
            	}catch(Exception e){
            		System.out.println("cashAmount: " + cashAmount);
            		System.out.println("totalNegotiatedCashAmount: " + totalNegotiatedCashAmount);
            		System.out.println("outstandingBalance: " + outstandingLCAmount);
            		amount = BigDecimal.ZERO;
            	}
            }      	
            else{
             amount = outstandingLCAmount;
            }
            cashAmount = amount;
            //addition
			
            DocumentType documentType = null;
            if(DocumentType.DOMESTIC.toString().equalsIgnoreCase(lcMap.get("documentType").toString())){
                documentType = DocumentType.DOMESTIC;
            } else {
                documentType = DocumentType.FOREIGN;
            }
            DocumentSubType1 documentSubType1 = null;
            DocumentSubType2 documentSubType2 = null;


            if(lcType.equalsIgnoreCase("CASH")){
                documentSubType1 = DocumentSubType1.CASH;
                documentSubType2 = DocumentSubType2.SIGHT;
            } else if(lcType.equalsIgnoreCase("STANDBY")){
                documentSubType1 = DocumentSubType1.STANDBY;
                documentSubType2 = DocumentSubType2.SIGHT;
            } else if(lcType.equalsIgnoreCase("REGULAR")){
                documentSubType1 = DocumentSubType1.REGULAR;
                if(lcTenor.equalsIgnoreCase("SIGHT")){
                    documentSubType2 = DocumentSubType2.SIGHT;
                } else if(lcTenor.equalsIgnoreCase("USANCE")) {
                    documentSubType2 = DocumentSubType2.USANCE;
                }
            }

            ServiceType serviceType = ServiceType.CANCELLATION;


            Map<String,Object> details = new HashMap<String, Object>();
            details.put("documentNumberStr",documentNumberStr);
            details.put("lcCurrency",lcCurrency);
            details.put("serviceType",serviceType);
            details.put("outstandingLCAmount",outstandingLCAmount);
            details.put("standbyTagging",lcMap.get("standbyTagging"));
			details.put("expiryDate",expiryDate);
			details.put("effectiveDate", reportDate);
            details.put("cashFlag", cashFlag);
            details.put("cashAmount", cashAmount);          
       	 	details.put("totalNegotiatedCashAmount", totalNegotiatedCashAmount);
       	 	details.put("currentAmount", currentAmount);
       	 	details.put("totalnegoAmount", totalnegoAmount);
			details.put("urr", urr);
		
            Map discrepancy = (Map) lcMap.get("negotiationDiscrepancies");
            if(discrepancy!=null && discrepancy.size()>0){
                details.put("withDiscrepancy","Y");
            } else {
                details.put("withDiscrepancy","N");
            }


            System.out.println("ratesService.getAngolConversionRate():"+ratesService.getAngolConversionRate(lcCurrency,"USD",2));

            //TODO INSERT RATE HERE
            if(lcCurrency.equalsIgnoreCase("PHP")){
                System.out.println("LC PHP");
                ratesService.getUrrConversionRateToday();

            } else if(lcCurrency.equalsIgnoreCase("USD")){
                System.out.println("LC USD");
                details.put("urr",ratesService.getUrrConversionRateToday());
            } else {
                System.out.println("LC THIRD");


                details.put("urr",ratesService.getUrrConversionRateToday());
                details.put("thirdToUSD",ratesService.getAngolConversionRate(lcCurrency,"USD",2));
            }

            System.out.println("details:"+details);
            String gltsNumber = gltsSequenceRepository.getGltsSequence();
            System.out.println("gltsNumber:"+gltsNumber);
            accountingService.generateActualEntriesForTradeProductCancellation(details, gltsNumber, documentClass, documentType, documentSubType1, documentSubType2, serviceType);
            gltsSequenceRepository.incrementGltsSequence();
            System.out.println("OUT");
            if(lcType.equalsIgnoreCase(LCType.REGULAR.toString()) || lcType.equalsIgnoreCase(LCType.STANDBY.toString())){
                // Un-earmark
                System.out.println("\n$$$ FACILITY UN-EARMARKING (Expired LCs) :::::::::\n");
                facilityService.unearmarkAvailment(documentNumberStr);
            }





//            tradeProductRepository.expireDocNum(documentNumberStr);

            /*
            DocumentNumber docNum = new DocumentNumber(documentNumberStr);
            LetterOfCredit lc = (LetterOfCredit) tradeProductRepository.load(docNum);
            Currency lcCurrency =  lc.getCurrency();
            System.out.println("lc.getCurrency():"+lc.getCurrency());
            System.out.println("lc.getUrr():"+lc.getUrr());
            System.out.println("lc.getSpecialRateThirdToUsd():"+lc.getSpecialRateThirdToUsd());
            System.out.println("lc.getPassOnRateThirdToUsd():"+lc.getPassOnRateThirdToUsd());
            lc.updateStatus(TradeProductStatus.EXPIRED);
            lc.updateLastModifiedDate();


            LCDocumentType lcDocumentType = lc.getDocumentType(); //DOCUMENT TYPE FX or DM
            LCType lcType = lc.getType(); //REGULAR("25"), CASH("0"), STANDBY("20"), DEFFERED("10"), REVOLVING("0");
            LCTenor lcTenor = lc.getTenor();  //SIGHT("Sight"), USANCE("Usance"); //, OTHER("Other");
            BigDecimal outstandingLCAmount = lc.getOutstandingBalance();

            DocumentClass documentClass = DocumentClass.LC;
            DocumentType documentType = null;
            DocumentSubType1 documentSubType1 = null;
            DocumentSubType2 documentSubType2 = null;

            if(LCDocumentType.DOMESTIC.equals(lcDocumentType)){
                documentType = DocumentType.DOMESTIC;
            } else if(LCDocumentType.FOREIGN.equals(lcDocumentType)) {
                documentType = DocumentType.FOREIGN;
            }

            if(lcType.equals(LCType.CASH)){
                documentSubType1 = DocumentSubType1.CASH;
                documentSubType2 = DocumentSubType2.SIGHT;
            } else if(lcType.equals(LCType.STANDBY)){
                documentSubType1 = DocumentSubType1.STANDBY;
                documentSubType2 = DocumentSubType2.SIGHT;
            } else if(lcType.equals(LCType.REGULAR)){
                documentSubType1 = DocumentSubType1.REGULAR;
                if(lcTenor.equals(LCTenor.SIGHT)){
                    documentSubType2 = DocumentSubType2.SIGHT;
                } else if(lcTenor.equals(LCTenor.USANCE)) {
                    documentSubType2 = DocumentSubType2.USANCE;
                }
            }

            ServiceType serviceType = ServiceType.CANCELLATION;


            Map<String,Object> details = new HashMap<String, Object>();
            details.put("documentNumberStr",documentNumberStr);
            details.put("lcCurrency",lcCurrency.getCurrencyCode());
            details.put("serviceType",serviceType);
            details.put("outstandingLCAmount",outstandingLCAmount);
            details.put("standbyTagging",lc.getStandbyTagging());
            if(lc.getNegotiationDiscrepancies()!=null &&lc.getNegotiationDiscrepancies().size()>0){
                details.put("withDiscrepancy","Y");
            } else {
                details.put("withDiscrepancy","N");
            }


            System.out.println("ratesService.getAngolConversionRate():"+ratesService.getAngolConversionRate(lcCurrency.getCurrencyCode(),"USD",2));

            //TODO INSERT RATE HERE
            if(lcCurrency.getCurrencyCode().equalsIgnoreCase("PHP")){
                System.out.println("LC PHP");
                ratesService.getUrrConversionRateToday();

            } else if(lcCurrency.getCurrencyCode().equalsIgnoreCase("USD")){
                System.out.println("LC USD");
                details.put("urr",ratesService.getUrrConversionRateToday());
            } else {
                System.out.println("LC THIRD");


                details.put("urr",ratesService.getUrrConversionRateToday());
                details.put("thirdToUSD",ratesService.getAngolConversionRate(lcCurrency.getCurrencyCode(),"USD",2));
            }

            System.out.println("details:"+details);
            String gltsNumber = gltsSequenceRepository.getGltsSequence();
            System.out.println("gltsNumber:"+gltsNumber);
            accountingService.generateActualEntriesForTradeProductCancellation(details, gltsNumber, documentClass, documentType, documentSubType1, documentSubType2, serviceType);
            gltsSequenceRepository.incrementGltsSequence();

//            if(lcType.equals(LCType.REGULAR) || lcType.equals(LCType.STANDBY)){
//                // Un-earmark
//                System.out.println("\n$$$ FACILITY UN-EARMARKING (Expired LCs) :::::::::\n");
//                facilityService.unearmarkAvailment(documentNumberStr);
//            }

            tradeProductRepository.persist(lc);

           */
        }
    }


    public void setTradeProductRepository(TradeProductRepository tradeProductRepository){
        this.tradeProductRepository = tradeProductRepository;
    }

    public void setAccountingService(AccountingService accountingService){
        this.accountingService = accountingService;
    }

    public void setGltsSequenceRepository(GltsSequenceRepository gltsSequenceRepository) {
        this.gltsSequenceRepository = gltsSequenceRepository;
    }

    public void setRatesService(RatesServiceImpl ratesService) {
        this.ratesService = ratesService;
    }

    public void setTfsDataSource(DataSource tfsDataSource) {
        this.tfsDataSource = tfsDataSource;
    }
}
