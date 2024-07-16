package com.ucpb.tfs.domain.service;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.ucpb.tfs.domain.payment.Payment;

import com.ucpb.tfs.batch.report.dw.ItrsRecord;
import com.ucpb.tfs.batch.report.dw.dao.TradeProductDao;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;

public class ItrsService {


	private TradeProductDao tradeProductDao;

    public void setTradeProductDao(TradeProductDao tradeProductDao) {
        this.tradeProductDao = tradeProductDao;
    }

    private PaymentRepository paymentRepository;

    private TradeServiceRepository tradeServiceRepository;
    
    private int SCH04_SELFFUNDED = 11;

	private String[] REPORT_HEADERS = {"BKCODE", "TRDATE", "TRCODE", "BOOKCD",
			"BSRATE", "CURCDE1", "AMTORIG1", "CURCDE2", "AMTORIG2", "CTRYCD", "DEALDT",
            "DEALCD",  "EXPCDE", "DBTCDE", "REMNME", "INVRNME", "TIN1",
            "SECRNO1", "DTIRNO1", "PSIC1", "IMPCDE", "CRDCDE", "BENENME", "INVSNME",
            "TIN2", "SECRNO2", "DTIRNO2", "PSIC2", "COMCDE", "MODPAY", "HCTRYCD", "IMPSCD",
            "LCNO", "BRN", "BILDTE", "OMATDTE", "NMATDTE", "REMCHLCDE", "PRVCDE", "OWNCDE",
            "BSRDNO", "CIRNO", "TTFNO", "LISTCDE", "REGTYPCDE", "ISIN", "SECNME", "RESDNCE",
            "CTPBCD", "CTPNME", "CTPTYPCDE", "SVLDTE", "SFRATE",
            "FFXDTE", "FVLDTE", "FFXRATE", "BSPDAN", "CFCCODE", "PSIC3"};

    public void setPaymentRepository(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }


    public void setTradeServiceRepository(TradeServiceRepository tradeServiceRepository) {
		this.tradeServiceRepository = tradeServiceRepository;
	}

	public void execute(String reportDate, String directory)throws Exception {
        int reportDay = Integer.parseInt(reportDate.split("-")[2]);
        int transactionDate = Integer.parseInt(reportDate.replace("-", "").replace("00.00.00", ""));
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date;

        try {
            date = format.parse(reportDate);
            tradeProductDao.deleteItrsRecords(reportDay);

        } catch (ParseException e) {
            e.printStackTrace();
            throw e;
        }
        System.out.println("Start sched 3...");
        List<ItrsRecord> sched3 = tradeProductDao.getSchedule3(date);

        for(ItrsRecord sched3Record : sched3){
            String remarks = sched3Record.getRemark();
            sched3Record.setTransactionDate(transactionDate);
            if(remarks == null)
                remarks = "";

			if (sched3Record.getBankCode() == null)
                remarks = remarks + "Bank code is null; ";

            if (sched3Record.getTransactionDate() == null)
                remarks = remarks + "Transaction date is null; ";
			
            if (sched3Record.getTransactionCode() == null)
                remarks = remarks + "Transaction Code is null; ";

            if (sched3Record.getCurrency() == null)
                remarks = remarks + "Currency code is null; ";

            if (sched3Record.getAmount() == null)
                remarks = remarks + "Amount is null; ";

            if (sched3Record.getCountryCode() == null)
                remarks = remarks + "Country code is null; ";

            if (sched3Record.getImporterCode() == null){
                remarks = remarks + "Importer participant code is null; ";
            }else if (!isValidParticipantCode(sched3Record.getImporterCode())){
                remarks = remarks + "Importer participant code is invalid; ";
            }

            if (sched3Record.getTinNumber2() == null)
                remarks = remarks + "TIN is null; ";

            if (sched3Record.getPaymentMode() == null)
                remarks = remarks + "Mode of payment is null; ";

            if (sched3Record.getImportStatusCode() == null)
                remarks = remarks + "Import status code is null; ";

			if (sched3Record.getLcNumber() == null && sched3Record.getReferenceNumber() == null){
                remarks = remarks + "Reference number is null; ";
			}
			
			if (sched3Record.getPaymentMode() == 4 && sched3Record.getAcceptanceDate() == null){
                remarks = remarks + "Acceptance date is null for DA; ";
			}

			sched3Record.setRemark(remarks);
			System.out.println("Insert sched 3 record : " + sched3Record.getLcNumber() + sched3Record.getReferenceNumber());
            tradeProductDao.insertSched3(sched3Record, reportDay);
        }

        System.out.println("Start sched 2...");
        List<ItrsRecord> sched2 = tradeProductDao.getSchedule2(date);

		for (ItrsRecord sched2Record : sched2) {
            sched2Record.setTransactionDate(transactionDate);
            Payment payment = paymentRepository.get(new TradeServiceId(sched2Record.getTradeserviceId()), ChargeType.SETTLEMENT);
           
            if (payment != null) {
            	 Set<PaymentDetail> paymentDetails = payment.getDetails();

                 for(PaymentDetail paymentDetail : paymentDetails){
                     if((paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.CASA) || paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.REMITTANCE) ||
                             paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.MC_ISSUANCE) || paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.IBT_BRANCH)) &&
                             paymentDetail.getCurrency().toString().equals("PHP")) {
                         sched2Record.setBookCode(1);
                     } else {
                         sched2Record.setBookCode(2);
                     }
                 }
            } else {
            	sched2Record.setBookCode(2);
            }
           
            String remarks = sched2Record.getRemark();
            if(remarks == null)
                remarks = "";

            if (sched2Record.getBankCode() == null)
                remarks = remarks + "Bank code is null; ";

            if (sched2Record.getTransactionDate() == null)
                remarks = remarks + "Transaction date is null; ";

            if (sched2Record.getTransactionCode() == null)
                remarks = remarks + "Transaction code is null; ";

            if (sched2Record.getCurrency() == null)
                remarks = remarks + "Currency code is null; ";

            if (sched2Record.getAmount() == null)
                remarks = remarks + "Amount is null; ";

            if (sched2Record.getCountryCode() == null)
                remarks = remarks + "Country code is null; ";

            if (sched2Record.getExporterCode() == null){
                remarks = remarks + "Exporter participant code is null; ";
            }else if (!isValidParticipantCode(sched2Record.getExporterCode())){
                remarks = remarks + "Exporter participant code is invalid; ";
             }

            if (sched2Record.getTinNumber1() == null)
                remarks = remarks + "TIN is null; ";

            if (sched2Record.getPaymentMode() == null)
                remarks = remarks + "Mode of Payment is null; ";

            if (sched2Record.getCommodityCode() == null || sched2Record.getCommodityCode().equalsIgnoreCase(""))
                remarks = remarks + "Commodity code is null; ";

            if (sched2Record.getBookCode() == null)
                remarks = remarks + "Book code is null; ";

            sched2Record.setRemark(remarks);
            System.out.println("Insert sched 2 record : " + sched2Record.getDocumentNumber());
            tradeProductDao.insertSched2(sched2Record, reportDay);
        }
		
        System.out.println("Start sched 4...");
        List<ItrsRecord> sched4 = tradeProductDao.getSchedule4(date);

        for (ItrsRecord sched4Record : sched4) {
            sched4Record.setTransactionDate(transactionDate);
            Payment payment = paymentRepository.get(new TradeServiceId(sched4Record.getTradeserviceId()), ChargeType.PRODUCT);
            
            if (payment != null) {
            	Set<PaymentDetail> paymentDetails = payment.getDetails();
                for(PaymentDetail paymentDetail : paymentDetails){
                	if((paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.CASA) && !paymentDetail.getCurrency().toString().equals("PHP")) ||
                			(paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.REMITTANCE) && !paymentDetail.getCurrency().toString().equals("PHP"))){
                		sched4Record.setBookCode(2);
                        sched4Record.setPaymentMode(SCH04_SELFFUNDED);
                        sched4Record.setLcNumber(null);
                        sched4Record.setReferenceNumber(null);
                        sched4Record.setAcceptanceDate(null);
                	} else {
                		sched4Record.setBookCode(1);
                	}            	                	              	
                	
                	if(sched4Record.getAmount() == null || sched4Record.getAmount().compareTo(BigDecimal.ZERO)==0) {
                		sched4Record.setAmount(paymentDetail.getAmountInLcCurrency());
                	}
                }
                
            } else {
            	sched4Record.setBookCode(2);
                sched4Record.setPaymentMode(SCH04_SELFFUNDED);
                sched4Record.setLcNumber(null);
                sched4Record.setReferenceNumber(null);
                sched4Record.setAcceptanceDate(null);   
            }
            
            if (sched4Record.getOriginalMaturity() == 0){
            	TradeService ts = tradeServiceRepository.load(new TradeServiceId(sched4Record.getTradeserviceId()));
            	String negotiationNumber = ts.getDetails().get("negotiationNumber").toString();
            	PaymentDetail pd = paymentRepository.getUaLoanPayment(negotiationNumber);
            	Date maturityDate = pd.getLoanMaturityDate();
            	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            	String maturityDateStr  = dateFormatter.format(maturityDate);
            	maturityDateStr = maturityDateStr.replace("-", "");
            	System.out.println("UA Loan : " + maturityDateStr);
            	sched4Record.setOriginalMaturity(Integer.valueOf(maturityDateStr));
            }
            
            String remarks = sched4Record.getRemark();
            if(remarks == null)
                remarks = "";

            if (sched4Record.getBankCode() == null)
                remarks = remarks + "Bank code is null; ";

            if (sched4Record.getTransactionDate() == null)
                remarks = remarks + "Transaction date is null; ";

            if (sched4Record.getTransactionCode() == null)
                remarks = remarks + "Transaction code is null; ";

            if (sched4Record.getCurrency() == null)
                remarks = remarks + "Currency code is null; ";

            if (sched4Record.getAmount() == null)
                remarks = remarks + "Amount is null; ";

            if (sched4Record.getCountryCode() == null)
                remarks = remarks + "Country code is null; ";

            if (sched4Record.getBookCode() == null)
                remarks = remarks + "Book code is null; ";

            if (sched4Record.getPaymentMode() == null)
                remarks = remarks + "Mode of Payment is null; ";

            if (sched4Record.getCommodityCode() == null || sched4Record.getCommodityCode().equalsIgnoreCase(""))
                remarks = remarks + "Commodity code is null; ";

            if (sched4Record.getImporterCode() == null){
                remarks = remarks + "Importer participant code is null; ";
            }else if (!isValidParticipantCode(sched4Record.getImporterCode())){
                remarks = remarks + "Importer participant code is invalid; ";
            }

            if (sched4Record.getOriginalMaturity() == null){
                remarks = remarks + "Original Maturity Date is null; ";
            }
			
            sched4Record.setRemark(remarks);
            System.out.println("Insert sched 4 record : "+ sched4Record.getLcNumber() + sched4Record.getReferenceNumber());
            tradeProductDao.insertSched4(sched4Record, reportDay);

        }

        format = new SimpleDateFormat("yyyyMMdd");
        String strDate  = format.format(date);
        System.out.println(directory);
        System.out.println("Start creating file...");
        File location = new File(directory);
        if(!location.exists())
            location.mkdirs();
        File report = new File(location, "TF" + strDate + ".csv" );
        PrintWriter writer = null;
        try {
        	writer = new PrintWriter(report);
            List<Map<String, Object>> itrsRecords = tradeProductDao.getItrsRecords(reportDay);

            for(int i = 0; i < REPORT_HEADERS.length; i++) {
                writer.append(REPORT_HEADERS[i]);
                if(REPORT_HEADERS.length - 1 == i){
                    writer.print("\r\n");
                } else {
                    writer.print(",");
                }
                writer.flush();
            }
            
            if(itrsRecords != null && itrsRecords.size() > 0) {
                Map<String, Object> record;
                for(int i = 0; i < itrsRecords.size(); i++) {
                    record = itrsRecords.get(i);
                    for (int a = 0; a < REPORT_HEADERS.length; a++) {
                        if(record.get(REPORT_HEADERS[a]) != null){
                            writer.print(record.get(REPORT_HEADERS[a]).toString());
                        } 
                        if(REPORT_HEADERS.length - 1 == a){
                            writer.print("\r\n");
                        } else {
                            writer.print(",");
                        }
                    }
                    writer.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            System.out.println("End creating file...");
            if(writer != null){
            	writer.close();
            }
        }
    }

	public boolean isValidParticipantCode(String str) {
		System.out.println("+++ParticipantCode: " + str);  
		str = str.toLowerCase();

		char[] charArray = str.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			char ch = charArray[i];
			if ((ch >= 'a' && ch <= 'z')) {
				System.out.println("+++ParticipantCode is invalid.");
				return false;
				}
			}
		System.out.println("+++ParticipantCode is valid.");
		return true;
		}
	}

