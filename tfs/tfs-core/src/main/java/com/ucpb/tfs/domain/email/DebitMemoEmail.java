package com.ucpb.tfs.domain.email;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ucpb.tfs.domain.cdt.RefPas5Client;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;

public class DebitMemoEmail implements Email{
	
	private final String SUBJECT = " - Copy of Debit Memo from CDT Payment";
	
	private List<String> to = new ArrayList<String>();
	private List<String> cc = new ArrayList<String>();
	
	private String content;
	private Map<String, Object> details;
	
	private RefPas5Client refPas5Client;
	
	public DebitMemoEmail(RefPas5Client refPas5Client, Map<String, Object> details){
		this.refPas5Client = refPas5Client;
		this.details = details;
	}
	
	@Override
	public String getSubject() {
		return SUBJECT;
	}

	@Override
	public void setTo() {
		if(refPas5Client.getEmail()!=null){
			to.add(refPas5Client.getEmail());
		}
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
	
	public void constructContent(){
		
		SimpleDateFormat printFormat = new SimpleDateFormat("MMMM dd,yyyy hh:mm a");
		DecimalFormat df = new DecimalFormat("#,###.00");
		
		String ied = (String) (details.get("iedieirdNo") != null ?  details.get("iedieirdNo") : "");
		String paidDate = (String) (details.get("paidDate") != null ?  printFormat.format(details.get("paidDate")) : "");
		String clientName = (String) (details.get("clientName") != null ?  details.get("clientName") : "");
		String accountNumber = (String) (details.get("accountNumber") != null ?  details.get("accountNumber") : "");
		String currency = (String) (details.get("currency") != null ?  details.get("currency") : "");
		String amount = (String) (details.get("amount") != null ?  df.format(details.get("amount")).toString() : "");
		String bankCharge = (String) (details.get("bankCharge") != null ?  df.format(details.get("bankCharge")).toString() : "");
	
		setTo();
		
		StringBuilder contentBuilder = new StringBuilder();
		
		contentBuilder.append("<html><head></head><body>");
		contentBuilder.append("This is an auto-generated message, Please do not reply.</br>");
		contentBuilder.append("<hr>");
		contentBuilder.append("Below are the details of your paid Custom Duties and Taxes: ");
		contentBuilder.append("<br/><br/>");
		contentBuilder.append("<pre>");
		contentBuilder.append("  Date paid         : " + paidDate + "<br/>");
		contentBuilder.append("  Client Name       : " + clientName + "<br/>");
		contentBuilder.append("  Account Number    : " + accountNumber + "<br/>");
		contentBuilder.append("  Currency/Amount   : " + currency + " " + amount + "<br/>");
		contentBuilder.append("  Particulars       : " + "Payment for Custom Duties and Taxes " +"<br/>");
		if(details.get("bankCharge")!=null){
			contentBuilder.append("                      " + "With Bank Comm: " + bankCharge + "</br>");
		}
		contentBuilder.append("                      " + "Under IED/IEIRD No." + ied + "<br/>");
		contentBuilder.append("<br/>");
		contentBuilder.append("</pre>");
		contentBuilder.append("<hr>");
		contentBuilder.append("For more information, you may contact the recipients in Cc.");
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
		if (refPas5Client.getBranchEmail()!=null){
			cc.add(refPas5Client.getBranchEmail());
		}
		if (refPas5Client.getRmbmEmail()!=null){
			cc.add(refPas5Client.getRmbmEmail());
		}		
	}

	@Override
	public List<String> getCc() {
		return cc;
	}

}
