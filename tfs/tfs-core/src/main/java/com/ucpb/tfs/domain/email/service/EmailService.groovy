package com.ucpb.tfs.domain.email.service

import java.util.Map;

import com.ucpb.tfs.domain.email.Email
import com.ucpb.tfs.domain.email.MailFrom
import com.ucpb.tfs.domain.email.SmtpAuthenticator
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus
import com.ucpb.tfs.domain.security.Employee

import org.springframework.mail.MailException
import org.springframework.mail.MailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.mail.javamail.JavaMailSenderImpl

import javax.mail.*
import javax.mail.internet.*

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PropertiesLoaderUtils
import org.springframework.mail.MailException
import org.springframework.mail.javamail.MimeMessageHelper

import com.ucpb.tfs.domain.email.Email
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus

/**
	(revision)
SCR/ER Number:
SCR/ER Description:
[Revised by:] Cedrick Nungay
[Date deployed:]
Program [Revision] Details: Change setting the value of subject on Upload Client
PROJECT: CORE
MEMBER TYPE  : JAVA

*/

public class EmailService {

	private final SUBJECT = "TFS Notification"
	
	/**
	 * 	05/26/2017 Redmine #4222
	 * 	Added by Pat - Added final string for subject of rerouting e-mail
	 */
	private final String REROUTINGSUBJECT = " - Rerouting";
	
	private String defaultAddress;
	private String enableNotification;

	public EmailService() {
		Resource resource = new ClassPathResource("/tfs.properties");
		Properties props = PropertiesLoaderUtils.loadProperties(resource);
		this.defaultAddress = props.get("mail.smtp.defaultAddress");
		this.enableNotification = props.get("mail.smtp.enableNotification");
	}
	
	public void setDefaultAddress(String defaultAddress) {
		this.defaultAddress = defaultAddress;
	}
	public void setEnableNotification(String enableNotification) {
		this.enableNotification = enableNotification;
	}

	public void sendEmail(smtpAuthentication, mailFrom, mailSender,Email mailDetails) {
		
		if (this.enableNotification.equalsIgnoreCase("true")) {
			MimeMessage mimeMessage = mailSender.createMimeMessage()

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "ISO-8859-1");

			println mailFrom?.getFrom()
			//Set From
			helper.setFrom(mailFrom?.getFrom());

			//Set Subject
			helper.setSubject(SUBJECT + mailDetails?.getSubject());

			//Set Body of Email
			helper.setText(mailDetails?.getContent(),true);

			println mailDetails?.getTo()
			//Set To
			helper.setTo(getInternetAddresses(mailDetails?.getTo()));

			println ("Sending message");

			//Send email
			mailSender.send(mimeMessage);

			println ("Message sending successful.");
			} 
	}
	
	public void sendRerouteEmailFromOriginalRecipient(smtpAuthentication, mailFrom, mailSender, Email mailDetails, ServiceInstructionStatus txStatus, String routedTo) {
		
		if(this.enableNotification.equalsIgnoreCase("true")) {
			
			MimeMessage mimeMessage = mailSender.createMimeMessage()

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "ISO-8859-1");

			println mailFrom?.getFrom()
			//Set From
			helper.setFrom(mailFrom?.getFrom());

			//Set Subject
			helper.setSubject(SUBJECT + REROUTINGSUBJECT);

			//Set Body of Email
			helper.setText(mailDetails?.getRerouteContentFromOriginalRecipient(txStatus, routedTo), true);

			println mailDetails?.getTo()
			//Set To
			helper.setTo(getInternetAddresses(mailDetails?.getTo()));

			println ("Sending message");

			//Send email
			mailSender.send(mimeMessage);

			println ("Message sending successful.");
		}
	}
	public void sendRerouteEmailToNewRecipient(smtpAuthentication, mailFrom, mailSender, Email mailDetails, ServiceInstructionStatus txStatus, String routedTo) {
		
		if(this.enableNotification.equalsIgnoreCase("true")) {
			
			MimeMessage mimeMessage = mailSender.createMimeMessage()

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "ISO-8859-1");

			println mailFrom?.getFrom()
			//Set From
			helper.setFrom(mailFrom?.getFrom());

			//Set Subject
			helper.setSubject(SUBJECT + REROUTINGSUBJECT);

			//Set Body of Email
			helper.setText(mailDetails?.getRerouteContentToNewRecipient(txStatus, routedTo), true); 

			println mailDetails?.getTo()
			//Set To
			helper.setTo(getInternetAddresses(mailDetails?.getTo()));

			println ("Sending message");

			//Send email
			mailSender.send(mimeMessage);

			println ("Message sending successful.");
		}	
	}
	
	public void sendCdtEmail(smtpAuthentication, mailFrom, mailSender,Email mailDetails) {
		
		if (this.enableNotification.equalsIgnoreCase("true")) {
			MimeMessage mimeMessage = mailSender.createMimeMessage()

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "ISO-8859-1");

			println mailFrom?.getFrom()
			//Set From
			helper.setFrom(mailFrom?.getFrom());

			//Set Subject
			helper.setSubject(mailDetails?.getSubject());

			//Set Body of Email
			helper.setText(mailDetails?.getContent(),true);

			println mailDetails?.getTo()
			//Set To
			helper.setTo(getInternetAddresses(mailDetails?.getTo()));
			
			println mailDetails?.getCc()
			//Set CC
			if(mailDetails?.getCc()){
				helper.setCc(getInternetAddresses(mailDetails?.getCc()));
			}

			println ("Sending message");

			//Send email
			mailSender.send(mimeMessage);

			println ("Message sending successful.");
			}
	}

	private InternetAddress[] getInternetAddresses(List emails){
		InternetAddress[] mailAddresses = new InternetAddress[emails.size()];
		emails.eachWithIndex {mail, i ->
			mailAddresses[i] = new InternetAddress(mail)
		}
		return mailAddresses;
	}

	public String getDefaultAddress(){
		return this.defaultAddress
	}
}
