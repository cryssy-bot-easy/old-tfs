package com.ucpb.tfs.domain.cdt.services


import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.ucpb.tfs.domain.cdt.CDTPaymentHistory
import com.ucpb.tfs.domain.cdt.CDTPaymentHistoryRepository
import com.ucpb.tfs.domain.cdt.CDTPaymentRequest
import com.ucpb.tfs.domain.cdt.CDTPaymentRequestRepository
import com.ucpb.tfs.domain.cdt.EmailNotif;
import com.ucpb.tfs.domain.cdt.RefPas5Client
import com.ucpb.tfs.domain.cdt.RefPas5ClientRepository
import com.ucpb.tfs.domain.cdt.enums.CDTStatus
import com.ucpb.tfs.domain.cdt.enums.CollectionType
import com.ucpb.tfs.domain.cdt.enums.PaymentRequestType
import com.ucpb.tfs.domain.cdt.event.CDTTagAsPaidEvent
import com.ucpb.tfs.domain.cdt.event.PaymentHistoryUploadedEvent
import com.ucpb.tfs.domain.email.CDTEmail
import com.ucpb.tfs.domain.email.Email
import com.ucpb.tfs.domain.email.MailFrom
import com.ucpb.tfs.domain.email.SmtpAuthenticator
import com.ucpb.tfs.domain.email.service.EmailService
import com.ucpb.tfs.domain.security.EmployeeRepository
import com.ucpb.tfs.domain.service.TradeService
import com.ucpb.tfs.utils.ExcelBuilder
import com.ucpb.tfs.utils.UtilSetFields
import com.ucpb.tfs.utils.WriteFile

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.mail.MailSender
import org.springframework.stereotype.Component

import com.incuventure.ddd.domain.DomainEventPublisher
//import com.ucpb.tfs.domain.cdt.enums.E2MStatus
import com.ucpb.tfs.domain.product.event.CDTPaymentRequestPaidEvent

/**
 * Service called for Uploading of Today and Pending CDT Transactions
 */
 
 /**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-1206-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Allan Comboy and Lymuel Saul
	[Date Deployed:] 12/20/2016
	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: PAS5FilesLoaderService
 */
 
	/**
	 (revision)
	SCR/ER Number:
	SCR/ER Description: Add parameter on loadPaymentRequest()
	[Revised by:] Jonh Henry Alabin
	[Date deployed:]
	Program [Revision] Details: Added parameters (user role, Email and Full name) for formatting of Email Notification
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	
	*/
 
	/**
	 (revision)
	SCR/ER Number:
	SCR/ER Description: Add parameter on loadPaymentRequest()
	[Revised by:] Jonh Henry Alabin
	[Date deployed:]
	Program [Revision] Details: Added subject handling for Upload Client
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	
	*/

/**
	 (revision)
	SCR/ER Number:
	SCR/ER Description:
	[Revised by:] Cedrick Nungay
	[Date updated:] 01/11/2018
	Program [Revision] Details: Added saving of importer's email, branch email rmb/bm email.
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	
 */

@Component
class PAS5FilesLoaderService {

	@Autowired
	SmtpAuthenticator smtpAuthenticator;
	
	@Autowired
	MailFrom mailFrom;
	
	@Autowired
	MailSender mailSender;
	
	@Autowired
	EmployeeRepository employeeRepository;
	
    @Autowired
    RefPas5ClientRepository refPas5ClientRepository

    @Autowired
    CDTPaymentRequestRepository cdtPaymentRequestRepository

    @Autowired
    CDTPaymentHistoryRepository cdtPaymentHistoryRepository;

    @Autowired
    DomainEventPublisher eventPublisher;
	
	@Autowired
	PropertiesFactoryBean appProperties;
	
    public void loadClientFile(InputStream is) {

        new ExcelBuilder(is).eachLine([labels:true]) {

            HashMap rowData = new HashMap()

            rowData.put("tin", cell(0).toString().trim());
            rowData.put("clientName", cell(1).toString().trim());
            rowData.put("registrationDate", cell(2).toString().trim());
            rowData.put("agentBankCode", cell(3).toString().trim());
            rowData.put("ccn", cell(4).toString().trim());

            //RefPas5Client pas5Client = refPas5ClientRepository.load(rowData["ccn"]);
            RefPas5Client pas5Client = refPas5ClientRepository.load(rowData["agentBankCode"]);

            // if client is not yet existing
            if (pas5Client == null) {
                pas5Client = new RefPas5Client();
            }

            // set the fields ...
            UtilSetFields.copyMapToObject(pas5Client, rowData)

            // ... and persist
            refPas5ClientRepository.merge(pas5Client)
        }

    }

    public void loadClientFile(InputStream is, String userrole) {

        new ExcelBuilder(is).eachLine([labels:true]) {

            HashMap rowData = new HashMap()

            rowData.put("tin", cell(0).toString().trim());
            rowData.put("clientName", cell(1).toString().trim());
            rowData.put("registrationDate", cell(2).toString().trim());
            rowData.put("agentBankCode", cell(3).toString().trim());
            rowData.put("ccn", cell(4).toString().trim());

            //RefPas5Client pas5Client = refPas5ClientRepository.load(rowData["ccn"]);
            RefPas5Client pas5Client = refPas5ClientRepository.load(rowData["agentBankCode"]);

            // if client is not yet existing
            if (pas5Client == null) {
                pas5Client = new RefPas5Client();
            }

            // set the fields ...
            UtilSetFields.copyMapToObject(pas5Client, rowData)

            // ... and persist
            pas5Client.setUploadedBy(userrole)

            refPas5ClientRepository.merge(pas5Client)
        }

    }

    public void loadClientFile(InputStream is, String userrole, String unitCode) {

    	String status = ""
		println "HClientDfIle"
        new ExcelBuilder(is).eachLine([labels:true]) {

			status = cell(5).toString().trim().toUpperCase()
			
			if(!status.equalsIgnoreCase("REJECTED")){
				HashMap rowData = new HashMap()
				
				rowData.put("tin", cell(0).toString().trim());
				rowData.put("clientName", cell(1).toString().trim());
				rowData.put("registrationDate", cell(2).toString().trim());
				rowData.put("agentBankCode", cell(3).toString().trim());
				rowData.put("ccn", cell(4).toString().trim());
				
				//RefPas5Client pas5Client = refPas5ClientRepository.load(rowData["ccn"]);
				RefPas5Client pas5Client = refPas5ClientRepository.load(rowData["agentBankCode"]);
				
				// if client is not yet existing
				if (pas5Client == null) {
					pas5Client = new RefPas5Client();
				}
				
				// set the fields ...
				UtilSetFields.copyMapToObject(pas5Client, rowData)
				
				// ... and persist
				pas5Client.setUploadedBy(userrole)
				pas5Client.setUnitCode(unitCode)
				
				refPas5ClientRepository.merge(pas5Client)
			}
        }
    }


    public void loadClientFile(String filename) {

        def fis= new FileInputStream(filename)
        loadClientFile(fis)

    }


    // same as loadPayment but accepts an InputStream instead of filename
    // so we don't need to create a temporary file
//    public void loadPaymentRequest(InputStream is) {
//
//        new ExcelBuilder(is).eachLine([labels:true]) {
//
//            HashMap rowData = new HashMap()
//
//            String iedieirdNumber = cell(0).toString().trim();
//            String amount = cell(4).toString().trim();
//
//            rowData.put("iedieirdNumber", cell(0).toString().trim());
//
////            rowData.put("pchcDateReceived", cell(1).toString().trim());
//            rowData.put("pchcDateReceived", new Date().parse("yyyy-M-d H:m:s", cell(1).toString().trim()));
//
//            rowData.put("agentBankCode", cell(2).toString().trim());
//            rowData.put("clientName", cell(3).toString().trim());
//            rowData.put("amount", cell(4).toString().trim());
//
//            //rowData.put("paymentRequestType", cell(5).toString().trim()); // TODO: transform this
//            rowData.put("e2mStatus", cell(6).toString().trim()); // TODO: transform this
//
//            rowData.put("finalDutyAmount", cell(7).toString().trim());
//            rowData.put("finalTaxAmount", cell(8).toString().trim());
//            rowData.put("finalCharges", cell(9).toString().trim());
//            rowData.put("ipf", cell(10).toString().trim());
//
//            println rowData
//
//            CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.load(rowData["iedieirdNumber"]);
//
//            // if payment request is not yet existing
//            if (paymentRequest == null) {
//                paymentRequest = new CDTPaymentRequest(new Date());
//
//                // set our internal status
//                paymentRequest.setStatus(CDTStatus.NEW)
//            }
//
//            println iedieirdNumber
//            println "indicator " + iedieirdNumber[9]
//
//            // set payment request type
//            if (iedieirdNumber.charAt(9) == 'W') {
//                println "W"
//                paymentRequest.setPaymentRequestType(PaymentRequestType.IPF)
//            } else if (iedieirdNumber.charAt(9) == 'E') {
//                println "E"
//                paymentRequest.setPaymentRequestType(PaymentRequestType.EXPORT)
//            } else if (iedieirdNumber.charAt(9) == 'D') {
//                println "D"
//                paymentRequest.setPaymentRequestType(PaymentRequestType.ADVANCE)
//
//                amount = amount.replace(",","")
//
//                BigDecimal amountRequested = new BigDecimal(amount);
//
////                if(amountRequested == BigDecimal.ZERO) {
////                    paymentRequest.setStatus(CDTStatus.PAID);
////                }
//
//            } else {
//                println "C"
//                paymentRequest.setPaymentRequestType(PaymentRequestType.FINAL)
//            }
//
//
//
//            // set the fields ...
//            UtilSetFields.copyMapToObject(paymentRequest, rowData)
//
//            // ... and persist
//            cdtPaymentRequestRepository.merge(paymentRequest)
//        }
//
//    }

    public void loadPaymentRequest(InputStream is, String cdtBookCode) {

        new ExcelBuilder(is).eachLine([labels:true]) {

            HashMap rowData = new HashMap()

            String iedieirdNumber = cell(0).toString().trim();
            String amount = cell(4).toString().trim();

            rowData.put("iedieirdNumber", cell(0).toString().trim());

//            rowData.put("pchcDateReceived", cell(1).toString().trim());
            rowData.put("pchcDateReceived", new Date().parse("yyyy-M-d H:m:s", cell(1).toString().trim()));

            rowData.put("agentBankCode", cell(2).toString().trim());
            rowData.put("clientName", cell(3).toString().trim());
            rowData.put("amount", cell(4).toString().trim());

            //rowData.put("paymentRequestType", cell(5).toString().trim()); // TODO: transform this
            rowData.put("e2mStatus", cell(6).toString().trim()); // TODO: transform this

            rowData.put("finalDutyAmount", cell(7).toString().trim());
            rowData.put("finalTaxAmount", cell(8).toString().trim());
            rowData.put("finalCharges", cell(9).toString().trim());
            rowData.put("ipf", cell(10).toString().trim());

            println rowData

            CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.load(rowData["iedieirdNumber"]);

            // if payment request is not yet existing
            if (paymentRequest == null) {
                paymentRequest = new CDTPaymentRequest(new Date());

                // set our internal status
                paymentRequest.setStatus(CDTStatus.NEW)
            } else {
                if (paymentRequest.isRefunded()) {
                    BigDecimal newAmount = null;
                    try {
                        newAmount = new BigDecimal(rowData.get("amount"));
                    } catch(NumberFormatException nfe) {
                        // do nothing
                    }
                    paymentRequest.updateDetails(rowData.get("pchcDateReceived"), newAmount);

                    paymentRequest.setStatus(CDTStatus.NEW);
                }
            }

            println iedieirdNumber
            println "indicator " + iedieirdNumber[9]

            // set payment request type
            /*if (iedieirdNumber.charAt(9) == 'W') {
                println "W"
                paymentRequest.setPaymentRequestType(PaymentRequestType.IPF)
                paymentRequest.setTransactionCode("074");
            } else*/

             if (iedieirdNumber.contains(" E")) {
                println "E"
                paymentRequest.setPaymentRequestType(PaymentRequestType.EXPORT)
                paymentRequest.setTransactionCode("078");
            } else if (iedieirdNumber.charAt(9) == 'D') {
                println "D"
                paymentRequest.setPaymentRequestType(PaymentRequestType.ADVANCE)
                paymentRequest.setTransactionCode("072");

                amount = amount.replace(",","")

                BigDecimal amountRequested = new BigDecimal(amount);

//                if(amountRequested == BigDecimal.ZERO) {
//                    paymentRequest.setStatus(CDTStatus.PAID);
//                }

            } /*else {
                println "C"
                paymentRequest.setPaymentRequestType(PaymentRequestType.FINAL)
                paymentRequest.setTransactionCode("073");
            }*/

            println "rowData.get(\"finalCharges\") " + rowData.get("finalCharges")
            if (new BigDecimal(rowData.get("finalCharges").toString().replaceAll(",", "")) != BigDecimal.ZERO) {
                paymentRequest.setPaymentRequestType(PaymentRequestType.FINAL)
                paymentRequest.setTransactionCode("073");
            }

            println "rowData.get(\"ipf\") " + rowData.get("ipf")
            if (new BigDecimal(rowData.get("ipf").toString().replaceAll(",", "")) != BigDecimal.ZERO) {
                paymentRequest.setPaymentRequestType(PaymentRequestType.IPF)
                paymentRequest.setTransactionCode("074");
            }




            paymentRequest.setUnitCode(cdtBookCode);

            // set the fields ...
            UtilSetFields.copyMapToObject(paymentRequest, rowData)

            // ... and persist
            cdtPaymentRequestRepository.merge(paymentRequest)
        }

    }

    private CollectionType getCollectionTypeByParams(String paymentRequestType, BigDecimal amount, BigDecimal ipfAmount) {
        println "params: " + paymentRequestType + ", " + amount + "," + ipfAmount
        if ("Advance".equalsIgnoreCase(paymentRequestType)) {
            return CollectionType.BOC2;
        }

        if ("Final".equalsIgnoreCase(paymentRequestType)) {
            println "[1] " + amount.compareTo(new BigDecimal("115")) == 0
            if (amount.compareTo(new BigDecimal("115")) == 0) {
                return CollectionType.BOC4;
            } else {
                println "[2] " + amount.compareTo(ipfAmount) != 0
                if (amount.compareTo(ipfAmount) != 0) {
                    return CollectionType.BOC1;
                } else {
                    return CollectionType.BOC3;
                }
            }
        }
    }

	/**
	* Method that reads the excel, saves the details to CDTPaymentRequest Table and at the same time,
	* Sending email to the corresponding clients.
	* 
	* @param is - the uploaded Excel
	* @param cdtBookCode - Book Code
	* @param allocUnitCode  - Allocation Unit Code
	*/
    public void loadPaymentRequest(InputStream is, String cdtBookCode, String allocUnitCode, String userrole, String fullName, String email) {
		
		println "DIto ditp Dito"
		println userrole + " : userrole"
		def defaultUnit = appProperties.object.getProperty('tfs.cdt.default.bookingunit');
		//phoneNumber for TSD(from tfs.properties)
		def phoneNumber = appProperties.object.getProperty('cdt.employee.phonenumber');
		
        new ExcelBuilder(is).eachLine([labels:true]) {

            HashMap rowData = new HashMap()

            String iedieirdNumber = cell(0).toString().trim();
            String amount = cell(4).toString().trim();
			String paymentRequestType = cell(5).toString().trim();

            rowData.put("iedieirdNumber", iedieirdNumber);

//            rowData.put("pchcDateReceived", cell(1).toString().trim());
            rowData.put("pchcDateReceived", new Date().parse("yyyy-M-d H:m:s", cell(1).toString().trim()));

            rowData.put("agentBankCode", cell(2).toString().trim());
            rowData.put("clientName", cell(3).toString().trim());
            rowData.put("amount", cell(4).toString().trim().replaceAll(",", ""));

            rowData.put("paymentRequestType", paymentRequestType); // TODO: transform this
            rowData.put("e2mStatus", cell(6).toString().trim()); // TODO: transform this

            rowData.put("finalDutyAmount", cell(7).toString().trim().replaceAll(",", ""));
            rowData.put("finalTaxAmount", cell(8).toString().trim().replaceAll(",", ""));
            rowData.put("finalCharges", cell(9).toString().trim().replaceAll(",", ""));
            rowData.put("ipf", cell(10).toString().trim().replaceAll(",", "")); 
			rowData.put("userrole", userrole);
			rowData.put("fullName", fullName); 
			rowData.put("email", email);
			rowData.put("phoneNumber", phoneNumber);

            println rowData
			// if payment request is null meaning iedieirdnumber is not yet used in db
            CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.load(rowData["iedieirdNumber"]);
			RefPas5Client refPas5Client = refPas5ClientRepository.load((rowData["agentBankCode"])) 
			
            // if payment request is not yet existing
            if (paymentRequest == null) {
                paymentRequest = new CDTPaymentRequest(new Date());

                // set our internal status
                paymentRequest.setStatus(CDTStatus.NEW)
				
				/**
		    	 * 	05/26/2017 Redmine #4222
		    	 * 	Edit by Pat - Removed comment to enable e-mail notification upon upload of todays transaction
		    	 */
				
				EmailNotif emailNotifs = new EmailNotif();
				Set<EmailNotif> emailSet= new HashSet<EmailNotif>();
				List<String> finalEmailList = new ArrayList<String>();
				emailNotifs.setIedieirdNumber(iedieirdNumber)
				
				try {
					
					if (refPas5Client.getEmail()){
						EmailService emailService = new EmailService();
						
						String clientEmail1 = refPas5Client.getEmail();
						String clientEmail2 = refPas5Client.getRmbmEmail();
						String clientEmail3 = refPas5Client.getBranchEmail();
						String allEmail = clientEmail1 +" "+ clientEmail2 +" "+ clientEmail3;
						
						emailNotifs.setEmailAddress(allEmail)
                        paymentRequest.setImportersEmail(clientEmail1);
                        paymentRequest.setRmbmEmail(clientEmail2);
                        paymentRequest.setBranchEmail(clientEmail3);

						String subject = "Advance duties - "+ cell(3).toString().trim() +" - " + iedieirdNumber;
						if (paymentRequestType.equalsIgnoreCase("final")) {
							subject = "Final duties - "+ cell(3).toString().trim() +" - "  + iedieirdNumber;
						}
						
						Email mailDetails = new CDTEmail(refPas5Client, rowData, "CASA", subject);
						emailService.sendCdtEmail(smtpAuthenticator, mailFrom, mailSender, mailDetails);
						paymentRequest.setEmailed(Boolean.TRUE);
						emailNotifs.setEmailStatus("E-mail sent");
						emailNotifs.setSentTime(new Date());
					}
					
					
				} catch(Exception e) {
					//Did not throw runtime exception, to allow TFS to proceed with the transaction.
					e.printStackTrace();
					emailNotifs.setEmailStatus("E-mail not sent");
					emailNotifs.setSentTime(null);
                    paymentRequest.setEmailed(Boolean.FALSE);
				}
				
				emailSet.add(emailNotifs);
				paymentRequest.setEmailNotifs(emailSet);
				
         
//			# NOT IN USE
//			else {
//                if (paymentRequest.isRefunded()) {
//                    BigDecimal newAmount = null;
//                    try {
//                        newAmount = new BigDecimal(rowData.get("amount"));
//                    } catch(NumberFormatException nfe) {
//                        // do nothing
//                    }
//                    paymentRequest.updateDetails(rowData.get("pchcDateReceived"), newAmount);
//
//                    paymentRequest.setStatus(CDTStatus.NEW);
//                }
//            }
//			# NOT IN USE
//            println iedieirdNumber
//            println "indicator " + iedieirdNumber[9]

            // SET COLLECTION TYPE
            paymentRequest.setCollectionType(getCollectionTypeByParams(cell(5).toString().trim(),
                                                                       new BigDecimal(rowData.get("amount")),
                                                                       new BigDecimal(rowData.get("ipf"))));

            // set payment request type
            /*if (iedieirdNumber.charAt(9) == 'W') {
                println "W"
                paymentRequest.setPaymentRequestType(PaymentRequestType.IPF)
                paymentRequest.setTransactionCode("074");
            } else*/

             if (iedieirdNumber.contains(" E")) {
                println "E"
                paymentRequest.setPaymentRequestType(PaymentRequestType.EXPORT)
                paymentRequest.setTransactionCode("078");
            } else if (iedieirdNumber.charAt(9) == 'D') {
                println "D"
                paymentRequest.setPaymentRequestType(PaymentRequestType.ADVANCE)
                paymentRequest.setTransactionCode("072");

                amount = amount.replace(",","")

                BigDecimal amountRequested = new BigDecimal(amount);

//                if(amountRequested == BigDecimal.ZERO) {
//                    paymentRequest.setStatus(CDTStatus.PAID);
//                }

            } /*else {
                println "C"
                paymentRequest.setPaymentRequestType(PaymentRequestType.FINAL)
                paymentRequest.setTransactionCode("073");
            }*/

//            println "rowData.get(\"finalCharges\") " + rowData.get("finalCharges")
            println "rowData.get(\"finalDutyAmount\") " + rowData.get("finalDutyAmount")
//            if (new BigDecimal(rowData.get("finalCharges").toString().replaceAll(",", "")) != BigDecimal.ZERO) {
            if (new BigDecimal(rowData.get("finalDutyAmount").toString().replaceAll(",", "")) != BigDecimal.ZERO) {
                paymentRequest.setPaymentRequestType(PaymentRequestType.FINAL)
                paymentRequest.setTransactionCode("073");
            }

            println "rowData.get(\"ipf\") " + rowData.get("ipf")
            if (new BigDecimal(rowData.get("ipf").toString().replaceAll(",", "")) != BigDecimal.ZERO) {
                paymentRequest.setPaymentRequestType(PaymentRequestType.IPF)
                paymentRequest.setTransactionCode("074");
            }



			def CifNumber = refPas5Client.getCifNumber();
			def CcbdBranchUnitCode = refPas5Client.getCcbdBranchUnitCode();
			
			//for change
			println "defaultUnit.toString():  " + defaultUnit.toString()
			if(!cdtBookCode.equalsIgnoreCase("909")){
			paymentRequest.setAllocationUnitCode("58"+cdtBookCode);
			paymentRequest.setBranchUnitCode(cdtBookCode);
			
			if(refPas5Client.getCcbdBranchUnitCode() != null)
				paymentRequest.setOfficerCode(refPas5Client.getOfficerCode());
			
			}else if(cdtBookCode.equalsIgnoreCase("909")){
			
						if(refPas5Client.getCifNumber() != null){
							
							if(refPas5Client.getCcbdBranchUnitCode() != null)
								paymentRequest.setBranchUnitCode(refPas5Client.getCcbdBranchUnitCode());
								else
								paymentRequest.setBranchUnitCode(defaultUnit.toString());
								
								paymentRequest.setOfficerCode(refPas5Client.getOfficerCode());
								paymentRequest.setAllocationUnitCode(refPas5Client.getAllocationUnitCode());
							
								}
							else{
								paymentRequest.setBranchUnitCode(defaultUnit.toString());
								paymentRequest.setAllocationUnitCode("58"+defaultUnit.toString());
								
								}
			
			
			
			
			}
				
			
			
		
			
			
			//for change
			
					
			
			
			paymentRequest.setExceptionCode(refPas5Client.getExceptionCode());
            paymentRequest.setUnitCode(cdtBookCode);
//            paymentRequest.setAllocationUnitCode(allocUnitCode);

            // set the fields ...
            UtilSetFields.copyMapToObject(paymentRequest, rowData)

            // ... and persist
            cdtPaymentRequestRepository.merge(paymentRequest)
			}
        }

    }
	
    public void loadPaymentRequest(String filename) {

        def fis= new FileInputStream(filename)
        loadPaymentRequest(fis)

    }

    public void loadAbandoned(InputStream is) {

        new ExcelBuilder(is).eachLine([labels:true]) {

            HashMap rowData = new HashMap()

            rowData.put("iedieirdNumber", cell(0).toString().trim());

            rowData.put("dateAbandoned", new Date().parse("yyyy-M-d H:m:s", cell(1).toString().trim()));

//            print rowData

            CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.getPaymentRequestDetails(rowData["iedieirdNumber"]);

            // if client is not yet existing, ignore it
            if (paymentRequest == null) {
                // paymentRequest = new CDTPaymentRequest(new Date());
            }
            else {

                paymentRequest.setAbandonedDate(rowData["dateAbandoned"])
                paymentRequest.setE2mStatus("ABANDONED")

                // set the fields ...
                UtilSetFields.copyMapToObject(paymentRequest, rowData)

                // ... and persist
                cdtPaymentRequestRepository.merge(paymentRequest);
            }
        }
    }

    public void loadAbandoned(String filename) {

        def fis= new FileInputStream(filename)
        loadAbandoned(fis)

    }

    public void loadHistory(InputStream is) {
        println "loadHistory"
        BigDecimal paymentHistoryTotal = BigDecimal.ZERO;

        List<String> iedieirdNumberList = new ArrayList<String>();

        new ExcelBuilder(is).eachLine([labels:true]) {

            HashMap rowData = new HashMap()

//            rowData.put("iedieirdNumber", cell(2).toString().trim());
//            rowData.put("e2mStatus", cell(23).toString().toUpperCase().trim());

            rowData.put("iedieirdNumber", cell(0).toString().trim());
            rowData.put("e2mStatus", cell(7).toString().toUpperCase().trim());

            println "hello " + rowData["iedieirdNumber"] + " = " + cell(3).toString().replaceAll(",", "")

            try {
                paymentHistoryTotal = paymentHistoryTotal.add(new BigDecimal(cell(3).toString().replaceAll(",", "")))
            } catch (NumberFormatException nfe) {
                println "nfe"
                paymentHistoryTotal = paymentHistoryTotal.add(BigDecimal.ZERO);
            }

            println "finding :" + rowData["iedieirdNumber"]   + ":"

//            CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.getPaymentRequestDetails(rowData["iedieirdNumber"]);
            CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.getPaymentRequestDetails(rowData["iedieirdNumber"]);

            // if client is not yet existing, ignore it
            if (paymentRequest == null) {
//                paymentRequest = new CDTPaymentRequest(new Date());
            }
            else {

                // get all iedierdNumber matched
                iedieirdNumberList.add(rowData["iedieirdNumber"]);

                println "found: " + rowData["iedieirdNumber"]
                paymentRequest.setE2mStatus(rowData["e2mStatus"])

                // set the fields ...
                UtilSetFields.copyMapToObject(paymentRequest, rowData)
                paymentRequest.updatePaymentHistoryUpdatedDate();

                // ... and persist
                cdtPaymentRequestRepository.merge(paymentRequest);

//                if(E2MStatus.CONFIRMED.equals(paymentRequest.getE2mStatus())){
                if(paymentRequest.getE2mStatus() && paymentRequest.getE2mStatus().equalsIgnoreCase("CONFIRMED")){
                    println "confirmed..."
                    eventPublisher.publish(new CDTPaymentRequestPaidEvent(paymentRequest));

                    PaymentHistoryUploadedEvent paymentHistoryUploadedEvent = new PaymentHistoryUploadedEvent(paymentRequest.getIedieirdNumber(),
                            "C",
                            "200",
                            "ELE");
                    eventPublisher.publish(paymentHistoryUploadedEvent);
                }

            }
        }

        setPaymentHistoryTotals(iedieirdNumberList, paymentHistoryTotal);
    }

    public void loadHistory(InputStream is, String unitCode, Date confDate) {

        println "loadHistory "
//        BigDecimal historyAmount = BigDecimal.ZERO;

//        List<String> iedieirdNumberList = new ArrayList<String>();

        new ExcelBuilder(is).eachLine([labels:true]) {

            HashMap rowData = new HashMap()

//            rowData.put("iedieirdNumber", cell(2).toString().trim());
//            rowData.put("e2mStatus", cell(23).toString().toUpperCase().trim());

            rowData.put("iedieirdNumber", cell(0).toString().trim());
            rowData.put("e2mStatus", cell(7).toString().toUpperCase().trim());

            println "finding :" + rowData["iedieirdNumber"]   + ":" + cell(3).toString().replaceAll(",", "")


            try {
                BigDecimal historyAmount = new BigDecimal(cell(3).toString().replaceAll(",", ""))
							
                CDTPaymentHistory cdtPaymentHistory = cdtPaymentHistoryRepository.load(rowData.get("iedieirdNumber"), unitCode,confDate);
				

                if (cdtPaymentHistory == null) {
                    cdtPaymentHistory = new CDTPaymentHistory(rowData.get("iedieirdNumber"),
                            confDate,
                            historyAmount,
                            unitCode);

                    cdtPaymentHistoryRepository.persist(cdtPaymentHistory);
                } else {
                    cdtPaymentHistory.updateAmount(historyAmount);

                    cdtPaymentHistoryRepository.merge(cdtPaymentHistory);
                }
            } catch (NumberFormatException nfe) {
                println "nfe"
                nfe.printStackTrace()
            }

//            CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.getPaymentRequestDetails(rowData["iedieirdNumber"]);
            CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.getOwnPaymentRequestDetails(rowData["iedieirdNumber"], unitCode);
			
			
			
            // if client is not yet existing, ignore it
            if (paymentRequest == null) {
//                paymentRequest = new CDTPaymentRequest(new Date());
            }
            else if(!paymentRequest.getStatus().equals(CDTStatus.REMITTED) &&
					!paymentRequest.getStatus().equals(CDTStatus.SENTTOBOC) &&
					!paymentRequest.getStatus().equals(CDTStatus.REFUNDED)){
				
				println "checking:s " + rowData["iedieirdNumber"]
                // get all iedierdNumber matched
//                iedieirdNumberList.add(rowData["iedieirdNumber"]);
				
				
                paymentRequest.setE2mStatus(rowData["e2mStatus"])

                // set the fields ...
                UtilSetFields.copyMapToObject(paymentRequest, rowData)
//                paymentRequest.updatePaymentHistoryUpdatedDate();
				
				println "found: " + rowData["iedieirdNumber"]
				paymentRequest.setConfDate(confDate);
				
				if(paymentRequest.getDatePaymentHistoryUploaded() == null)
				paymentRequest.setDatePaymentHistoryUploaded(new Date());
					
                // ... and persist
                cdtPaymentRequestRepository.merge(paymentRequest);

//                try {
//                    paymentHistoryTotal = paymentHistoryTotal.add(new BigDecimal(cell(3).toString().replaceAll(",", "")))
//                } catch (NumberFormatException nfe) {
//                    println "nfe"
//                    paymentHistoryTotal = paymentHistoryTotal.add(BigDecimal.ZERO);
//                }

//                if(E2MStatus.CONFIRMED.equals(paymentRequest.getE2mStatus())){
                if(paymentRequest.getE2mStatus() && paymentRequest.getE2mStatus().equalsIgnoreCase("CONFIRMED")){
                    println "confirmed..."
                    eventPublisher.publish(new CDTPaymentRequestPaidEvent(paymentRequest));

                    PaymentHistoryUploadedEvent paymentHistoryUploadedEvent = new PaymentHistoryUploadedEvent(paymentRequest.getIedieirdNumber(),
                            "C",
//                            "200",
                            "A5490",
                            "ELE");
                    eventPublisher.publish(paymentHistoryUploadedEvent);
                }

				
            
            }
				
        }

//        setPaymentHistoryTotals(iedieirdNumberList, paymentHistoryTotal);
//        setPaymentHistoryTotals(unitCode, paymentHistoryTotal);
    }

    private void setPaymentHistoryTotals(String unitCode, BigDecimal paymentHistoryTotal) {
        List<CDTPaymentRequest> allUploadedToday = cdtPaymentRequestRepository.getAllUploadedToday(unitCode);

        for (CDTPaymentRequest cdtPaymentRequest : allUploadedToday) {
            cdtPaymentRequest.setPaymentHistoryTotal(paymentHistoryTotal);

            cdtPaymentRequestRepository.merge(cdtPaymentRequest);
        }
    }

    private void setPaymentHistoryTotals(List<String> iedierdNumberMatched, BigDecimal paymentHistoryTotal) {
        for (String iedierdNumber: iedierdNumberMatched) {
            CDTPaymentRequest cdtPaymentRequest = cdtPaymentRequestRepository.load(iedierdNumber);

            cdtPaymentRequest.setPaymentHistoryTotal(paymentHistoryTotal);

            cdtPaymentRequestRepository.merge(cdtPaymentRequest);
        }
    }

    public void loadHistory(String filename) {

        def fis= new FileInputStream(filename)
        loadHistory(fis)

    }

    public void tagAsPaid(String iedieirdNumber) {
        println "tagging as paid"

        CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.load(iedieirdNumber);

        paymentRequest.tagAsPaid();

        cdtPaymentRequestRepository.merge(paymentRequest);
    }

    public void tagAsPaid(String iedieirdNumber, TradeService tradeService) {
        println "tagging as paid"

        CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.load(iedieirdNumber);

        paymentRequest.tagAsPaid();

        cdtPaymentRequestRepository.merge(paymentRequest);

        String processingUnitCode = tradeService.getDetails().get("processingUnitCode") ?
                (String) tradeService.getDetails().get("processingUnitCode").toString() :
                (String) tradeService.getDetails().get("unitCode").toString();

        CDTTagAsPaidEvent cdtPaidEvent = new CDTTagAsPaidEvent(iedieirdNumber, processingUnitCode, tradeService);
        eventPublisher.publish(cdtPaidEvent);
    }

    public void tagAsNew(String iedieirdNumber) {
        println "tagging as new"

        CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.load(iedieirdNumber);

        paymentRequest.tagAsNew();

        cdtPaymentRequestRepository.merge(paymentRequest);
    }

    public void tagAsPending() {
        println "tagging as pending"

//        List<CDTPaymentRequest> cdtPaymentRequestList = cdtPaymentRequestRepository.getNewPaymentRequestsYesterday();
        List<CDTPaymentRequest> cdtPaymentRequestList = cdtPaymentRequestRepository.getNewPaymentRequestsToday();

        for (CDTPaymentRequest cdtPaymentRequest : cdtPaymentRequestList) {
            println "iedieirdNumber >> " + cdtPaymentRequest.getIedieirdNumber();

            cdtPaymentRequest.tagAsPending();

            cdtPaymentRequestRepository.merge(cdtPaymentRequest);
        }
    }



    public List<Map<String, Object>> generateTrams(Date dateGenerated) {
        List<CDTPaymentRequest> allCDTsForTrams = new ArrayList<CDTPaymentRequest>();

        List<CDTPaymentRequest> confirmedPayments = cdtPaymentRequestRepository.getConfirmedPayments(dateGenerated);
        List<CDTPaymentRequest> rejectedPayments = cdtPaymentRequestRepository.getRejectedPayments(dateGenerated);

        allCDTsForTrams.addAll(confirmedPayments);
        allCDTsForTrams.addAll(rejectedPayments);

        return allCDTsForTrams;
    }
}