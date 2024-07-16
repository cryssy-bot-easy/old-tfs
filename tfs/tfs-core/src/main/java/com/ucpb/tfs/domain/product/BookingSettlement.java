package com.ucpb.tfs.domain.product;

import java.io.Serializable;

public class BookingSettlement implements Serializable {
	
	private String id;
	
	private DocumentNumber documentNumber;
	
	private String bookingAccountType;


    public BookingSettlement(){ }

	public BookingSettlement(DocumentNumber documentNumber, String bookingAccountType){
		this.documentNumber = documentNumber;
		
		this.bookingAccountType = bookingAccountType;
	}
	
	public DocumentNumber getDocumentNumber(){
		return documentNumber;
	}
	
	public String getBookingAccountType(){
		return bookingAccountType;
	}
	
	public void updateBookingAccountType(String bookingAccountType){
		this.bookingAccountType = bookingAccountType;
	}
	
}
