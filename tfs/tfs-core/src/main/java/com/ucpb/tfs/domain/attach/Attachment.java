package com.ucpb.tfs.domain.attach;

import java.io.Serializable;
import java.util.Date;

public class Attachment implements Serializable  {
	
	private Long id;
	private String filename;
	private String noderefid;
	private Date createdDate;
	private String attachmentType;
	
	public Attachment (){
		
	}
	
	public Attachment (String filename, String noderefid, Date createdDate, String attachmentType){
		
		this.filename = filename;
		this.noderefid = noderefid;
		this.createdDate = createdDate;
		this.attachmentType = attachmentType;
	}	
	
	public String getFilename(){
		return this.filename;
	}
	
	public String getNoderefid(){
		return this.noderefid;
	}
	
	public Date getCreatedDate(){
		return this.createdDate;
	}	
	
	public String getAttachmentType(){
		return this.attachmentType;
	}

    public Attachment duplicateAttachment() {
        Attachment attachment = new Attachment();

        attachment.filename = this.getFilename();
        attachment.noderefid = this.getNoderefid();
        attachment.createdDate = new Date();
        attachment.attachmentType = this.getAttachmentType();

        return attachment;
    }
}
