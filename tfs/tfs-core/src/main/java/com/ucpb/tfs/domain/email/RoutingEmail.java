package com.ucpb.tfs.domain.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.security.Employee;

public class RoutingEmail implements Email{

	private Employee employeeSender;
	private Employee employeeReceiver;
	
	private Map<String, Object> parameter;
	
	private List<String> to = new ArrayList<String>();
	private List<String> cc = new ArrayList<String>();
	
	private final String SUBJECT = " - Routing";
	
	private String content;
	
	public RoutingEmail(Map<String, Object> parameter, Employee employeeSender, Employee employeeReceiver){
		this.parameter = parameter;
		this.employeeSender = employeeSender;
		this.employeeReceiver = employeeReceiver;
	}

	@Override
	public String getSubject() {
		return SUBJECT;
	}

	@Override
	public void setTo() {
		if (employeeReceiver != null){
			to.add(employeeReceiver.getEmail());
		}
	}
	
	@Override
	public List<String> getTo() {
		return to;
	}
	
	@Override
	public String getContent() {
		
		String documentType = parameter.get("documentType") != null ? parameter.get("documentType").toString() : "";
		String documentSubType1 = parameter.get("documentSubType1") != null ? parameter.get("documentSubType1").toString() : "";
		String documentSubType2 = parameter.get("documentSubType2") !=null ? parameter.get("documentSubType2").toString() : "";
		String documentClass = parameter.get("documentClass") != null ? parameter.get("documentClass").toString() : "";
		String serviceType = parameter.get("serviceType") != null ? parameter.get("serviceType").toString() : "";
		
		String statusAction = parameter.get("statusAction") != null ? parameter.get("statusAction").toString() : "";
		
		String senderFullName = employeeSender.getFullName();
		String receiverFullName = employeeReceiver.getFullName();
		
		
		contructContent(getTransactionName(documentType, documentSubType1, documentSubType2, documentClass, serviceType),
						getRoutingStatus(statusAction),
						senderFullName,
						receiverFullName);
		
		return content;
	}
	
	@Override
	public String getRerouteContentFromOriginalRecipient(ServiceInstructionStatus txStatus, String routedTo) {
		
		String documentType = parameter.get("documentType") != null ? parameter.get("documentType").toString() : "";
		String documentSubType1 = parameter.get("documentSubType1") != null ? parameter.get("documentSubType1").toString() : "";
		String documentSubType2 = parameter.get("documentSubType2") !=null ? parameter.get("documentSubType2").toString() : "";
		String documentClass = parameter.get("documentClass") != null ? parameter.get("documentClass").toString() : "";
		String serviceType = parameter.get("serviceType") != null ? parameter.get("serviceType").toString() : "";
		
		String statusAction = parameter.get("statusAction") != null ? parameter.get("statusAction").toString() : "";
		
		String senderFullName = employeeSender.getFullName();
		String receiverFullName = employeeReceiver.getFullName();
		
		contructRerouteContentFromOriginalRecipient(getTransactionName(documentType, documentSubType1, documentSubType2, documentClass, serviceType),
						getRoutingStatus(statusAction),
						senderFullName,
						receiverFullName,
						txStatus,
						routedTo);
		
		
		return content;
	}
	@Override
	public String getRerouteContentToNewRecipient(ServiceInstructionStatus txStatus, String routedTo) {
		
		String documentType = parameter.get("documentType") != null ? parameter.get("documentType").toString() : "";
		String documentSubType1 = parameter.get("documentSubType1") != null ? parameter.get("documentSubType1").toString() : "";
		String documentSubType2 = parameter.get("documentSubType2") !=null ? parameter.get("documentSubType2").toString() : "";
		String documentClass = parameter.get("documentClass") != null ? parameter.get("documentClass").toString() : "";
		String serviceType = parameter.get("serviceType") != null ? parameter.get("serviceType").toString() : "";
		
		String statusAction = parameter.get("statusAction") != null ? parameter.get("statusAction").toString() : "";
		
		String senderFullName = employeeSender.getFullName();
		String receiverFullName = employeeReceiver.getFullName();
		
		contructRerouteContentToNewRecipient(getTransactionName(documentType, documentSubType1, documentSubType2, documentClass, serviceType),
						getRoutingStatus(statusAction),
						senderFullName,
						receiverFullName,
						txStatus,
						routedTo);
		
		
		return content;
	}
	
	private String getRoutingStatus(String statusAction){
		if (statusAction.toLowerCase().equals("prepare")){
			return "Prepared";
		}else if(statusAction.toLowerCase().equals("check")){
			return "Checked";
		}else if(statusAction.toLowerCase().equals("approve")){
			return "Approved";
		}else{
			return "Returned";
		}
	}
	
	private String getTransactionName(String documentType, String documentSubType1, String documentSubType2, String documentClass, String serviceType){
		String transactionName = "";
		if (documentType != null){
			transactionName += documentType.toUpperCase() + " ";
		}
		if (documentSubType1 != null ){
			transactionName += documentSubType1.toUpperCase() + " ";
		}
		if (documentSubType2 != null){
			transactionName += documentSubType2.toUpperCase() + " ";
		}
		if (documentClass != null){
			transactionName += documentClass.toUpperCase() + " ";
		}
		if (serviceType != null){
			transactionName += serviceType.toUpperCase();
		}
		return transactionName;
	}
	
	private void contructContent(String transactionName, String statusAction, String senderfullName, String receiverFullName){
		
		setTo();
		
		StringBuilder contentBuilder = new StringBuilder();
		
		contentBuilder.append("<html><head></head><body>");
		contentBuilder.append("This is an auto-generated message, Please do not reply.</br>");
		contentBuilder.append("<hr>");
		contentBuilder.append("A transaction <b>" + transactionName + "</b> has been tagged as <i>" + statusAction + "</i> by <i>" + senderfullName + "</i> and was routed to <i>" + "you" + "</i></br>");
		contentBuilder.append("<hr>");
		contentBuilder.append("Log in to TFS now.");
		contentBuilder.append("</body></html>");
		
		content = contentBuilder.toString();
	}
	
	private void contructRerouteContentFromOriginalRecipient(String transactionName, String statusAction, String senderfullName, String receiverFullName, ServiceInstructionStatus txStatus, String routedTo){
		
		setTo();
		
		StringBuilder contentBuilder = new StringBuilder();
		
		contentBuilder.append("<html><head></head><body>");
		contentBuilder.append("This is an auto-generated message, Please do not reply.</br>");
		contentBuilder.append("<hr>");
		contentBuilder.append("A transaction <b>" + transactionName + "</b> has been tagged as <i>" + txStatus + "</i> by <i>" + senderfullName + "</i> and was rerouted from <i>" + "you" + "</i> to <i>" + routedTo + "</i>.</br>");
		contentBuilder.append("<hr>");
		contentBuilder.append("Log in to TFS now.");
		contentBuilder.append("</body></html>");
		
		content = contentBuilder.toString();
	}
	private void contructRerouteContentToNewRecipient(String transactionName, String statusAction, String senderfullName, String receiverFullName, ServiceInstructionStatus txStatus, String routedTo){
		
		setTo();
		
		StringBuilder contentBuilder = new StringBuilder();
		
		contentBuilder.append("<html><head></head><body>");
		contentBuilder.append("This is an auto-generated message, Please do not reply.</br>");
		contentBuilder.append("<hr>");
	    contentBuilder.append("A transaction <b>" + transactionName + "</b> has been tagged as <i>" + txStatus + "</i> by <i>" + senderfullName + "</i> and was rerouted from <i>" + routedTo + "</i> to <i>" + "you" + "</i></br>");
		contentBuilder.append("<hr>");
		contentBuilder.append("Log in to TFS now.");
		contentBuilder.append("</body></html>");
		
		content = contentBuilder.toString();
	}

	@Override
	public void setCc() {
		// TODO Auto-generated method stub
		if (employeeSender != null){
			if (employeeSender.getReceiveEmail() != null && employeeSender.getReceiveEmail() == true){
				cc.add(employeeSender.getEmail());
			}
		}
	}

	@Override
	public List<String> getCc() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
