package com.ucpb.tfs.domain.cdt;

import java.util.Date;


//  PROLOGUE:
//  (New)
//  [Created by:] Rafael "Ski" Poblete
//  [Date Deployed:] 12/20/2016
//  Program [New] Details: Saving to new table EMAIL_NOTIF.
//  PROJECT: CORE
//  MEMBER TYPE  : Java
//  Project Name: EMAIL_NOTIF


public class EmailNotif {
	
	Long id;

	String iedieirdNumber;

    String emailAddress;

    String emailStatus;
    
    Date sentTime;
    
    public EmailNotif() { }    

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public String getIedieirdNumber() {
        return iedieirdNumber;
    }

    public void setIedieirdNumber(String iedieirdNumber) {
        this.iedieirdNumber = iedieirdNumber;
    }
    
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public String getEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(String emailStatus) {
        this.emailStatus = emailStatus;
    }
    
    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    
    
}
