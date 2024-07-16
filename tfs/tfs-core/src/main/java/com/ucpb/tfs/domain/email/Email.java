package com.ucpb.tfs.domain.email;

import java.util.List;

import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;

public interface Email {
	
	public String getSubject();

	public void setTo();
	
	public void setCc();
	
	public List<String> getTo();
	
	public List<String> getCc();
	
	public String getContent();

	public String getRerouteContentFromOriginalRecipient(ServiceInstructionStatus txStatus, String routedTo);
	
	public String getRerouteContentToNewRecipient(ServiceInstructionStatus txStatus, String routedTo);

}
