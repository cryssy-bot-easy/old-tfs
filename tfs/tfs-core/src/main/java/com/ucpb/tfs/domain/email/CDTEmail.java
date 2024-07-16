package com.ucpb.tfs.domain.email;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.ucpb.tfs.domain.cdt.RefPas5Client;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;

/**
	(revision)
SCR/ER Number: 
SCR/ER Description: Formatting of Email Body
[Revised by:] Jonh Henry Alabin
[Date deployed:] 
Program [Revision] Details: Added a format for cdtAmount, getting Email ,user role anf full name of the Uploader
PROJECT: CORE
MEMBER TYPE  : JAVA

*/

/**
	(revision)
SCR/ER Number: 
SCR/ER Description:
[Revised by:] Cedrick Nungay
[Date deployed:] 
Program [Revision] Details: Change handling on retrieval of subject
PROJECT: CORE
MEMBER TYPE  : JAVA

*/

public class CDTEmail implements Email{

	private String subject;
	
	private List<String> to = new ArrayList<String>();
	private List<String> cc = new ArrayList<String>();
	
	private String content;
	private String modeOfPayment;

	private HashMap<String, Object> rowData;
	
	private RefPas5Client refPas5Client;
//	private Employee employeeProcessor;
	
//	public CDTEmail(RefPas5Client refPas5Client, CDTPaymentRequest cdtPaymentRequest, Employee employeeProcessor, String modeOfPayment){
	public CDTEmail(RefPas5Client refPas5Client, HashMap<String, Object> rowData, String modeOfPayment){
		this.refPas5Client = refPas5Client;
		this.rowData = rowData;
//		this.employeeProcessor = employeeProcessor;
		this.modeOfPayment = modeOfPayment;
	}

	public CDTEmail(RefPas5Client refPas5Client, HashMap<String, Object> rowData, String modeOfPayment, String subject){
		this(refPas5Client, rowData, modeOfPayment);
		this.subject = subject;
	}
	
	@Override
	public String getSubject() {
		return subject;
	}

	@Override
	public void setTo() {
		System.out.println("Inside Set To: " + refPas5Client.getEmail());
		if (refPas5Client.getEmail() != null){
			int ctr = 0;
			List<String> emailList = loopThroughEmails();
			for (String emailTo : emailList){
				if(emailTo.contains("@")){
					if (ctr == 0){
						to.add(emailTo);
					} else if (ctr > 0){
						cc.add(emailTo);
					}
					ctr += 1;
					System.out.println("EmailTo : " + emailTo);
				}
			}
				
		}
		System.out.println("After: " + getTo());
	}
	
	@Override
	public List<String> getTo() {
		return to;
	}

	@Override
	public String getContent() {
		constructContent();
		return content;
	}
	

	public List<String> loopThroughEmails() {
		String clientEmail1 = refPas5Client.getEmail();
		String clientEmail2 = refPas5Client.getRmbmEmail();
		String clientEmail3 = refPas5Client.getBranchEmail();
		
		List<String> clientList1 = Arrays.asList(clientEmail1.split("[\\;\\,\\|\\s]"));
		List<String> clientList2 = Arrays.asList(clientEmail2.split("[\\;\\,\\|\\s]"));
		List<String> clientList3 = Arrays.asList(clientEmail3.split("[\\;\\,\\|\\s]"));
		
		ArrayList<String> clientList = new ArrayList<String>();
		clientList.addAll(clientList1);
		clientList.addAll(clientList2);
		clientList.addAll(clientList3);
		
		
		return clientList;	
	}
	
	private void constructContent(){
		
		DecimalFormat df = new DecimalFormat("#,##0.00");
		
		setTo();
		
		String ied = (String) (rowData.get("iedieirdNumber") !=null ? rowData.get("iedieirdNumber") : "");
		String aab = (String) (rowData.get("agentBankCode") !=null ? rowData.get("agentBankCode") : "");
		String importerName = (String) (rowData.get("clientName") !=null ? rowData.get("clientName") : "");
//		String requestType = (String) (rowData.get("paymentRequestType") !=null ? rowData.get("paymentRequestType") : "");
		String userRole = (String) (rowData.get("userrole") !=null ? rowData.get("userrole") : "");
		String fullName = (String) (rowData.get("fullName") !=null ? rowData.get("fullName") : "");
		String email = (String) (rowData.get("email") !=null ? rowData.get("email") : "");
		String phoneNumber = (String) (rowData.get("phoneNumber") !=null ? rowData.get("phoneNumber") : "");
		/**
		 * 	05/26/2017 Redmine #4222
		 * 	Edit by Pat - Removed formatting of numbers which causes exception
		 */
		String amount = (String) (rowData.get("amount") !=null ? ((rowData.get("amount"))) : "");
		Double cdtAmount = Double.parseDouble(amount);
//		String bankCharge = (String) (rowData.get("finalCharges") !=null ? (rowData.get("finalCharges")) : "");
//		String totalAmountDue = (String) (rowData.get("finalDutyAmount") !=null ? (rowData.get("finalDutyAmount")) : ""); 
		
		StringBuilder contentBuilder = new StringBuilder();
		
		contentBuilder.append("<html><head></head><body>");
		contentBuilder.append("This is a system-generated message.  Please follow instruction below.</br>");
		contentBuilder.append("<hr>");
		contentBuilder.append("We have received Request For Payment (RFP) from the Bureau of Customs ");
		contentBuilder.append("<br/>");
		contentBuilder.append("relative&nbsp; to&nbsp; the&nbsp; payment&nbsp; of&nbsp; customs&nbsp; duties&nbsp; and taxes&nbsp; of the&nbsp; following<br/>");
		contentBuilder.append("client, details as follows:");
		contentBuilder.append("<br/><br/>");
		contentBuilder.append("<pre>");
		contentBuilder.append("  IED/IEIRD No.          : "    + ied            + "<br/>");
		contentBuilder.append("  AAB-Ref No.            : "    + aab			   + "<br/>");
		contentBuilder.append("  Importer Name          : "    + importerName   + "<br/>");
		contentBuilder.append("  CDT Amount             : PHP " + df.format(cdtAmount)      + "<br/>");
		contentBuilder.append("  LBP Transmittal Fee    : PHP " + df.format(refPas5Client.getDefaultBankCharge())      + "<br/>");
		contentBuilder.append("  <b>TOTAL AMOUNT TO BE PAID</b>: PHP " + df.format(refPas5Client.getDefaultBankCharge().add(BigDecimal.valueOf(cdtAmount)))      + "<br/>");
		contentBuilder.append("</pre>");
		contentBuilder.append("<br/>Please&nbsp; confirm&nbsp; and send&nbsp; your&nbsp; debit&nbsp; instructions&nbsp; on or&nbsp; before&nbsp; 2:00pm, <br/>");
		contentBuilder.append("otherwise,&nbsp; transaction will&nbsp; be processed&nbsp; the next&nbsp; banking day.&nbsp; Likewise, <br/>");
		contentBuilder.append("all&nbsp; RFPs received&nbsp; beyond 2:00pm will be processed the next banking day.<br/>");
		contentBuilder.append("<hr>");
		if(userRole.equalsIgnoreCase("TSD")){
			contentBuilder.append("Please contact LBP International Trade Department " + fullName);
			contentBuilder.append("<br/>");
			contentBuilder.append(email);
			contentBuilder.append("<br/>");	
			contentBuilder.append(phoneNumber);
			contentBuilder.append("<br/>");	
		} else{
			contentBuilder.append("Please contact UCPB " + fullName);
			contentBuilder.append("<br/>");
			contentBuilder.append(email);
		}
		contentBuilder.append("</body></html>");
		
		content = contentBuilder.toString();
	}

	@Override
	public String getRerouteContentFromOriginalRecipient(ServiceInstructionStatus txStatus, String routedTo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRerouteContentToNewRecipient(
			ServiceInstructionStatus txStatus, String routedTo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCc() {
		if (refPas5Client.getRmbmEmail() != null){
			cc.add(refPas5Client.getRmbmEmail());
		}
		if (refPas5Client.getBranchEmail() != null){
			cc.add(refPas5Client.getBranchEmail());
		}
	}

	@Override
	public List<String> getCc() {
		return cc;
	}

}
