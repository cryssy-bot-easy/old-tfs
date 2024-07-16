package com.ucpb.tfs.domain.letter;

import com.ucpb.tfs.domain.letter.enumTypes.LetterType;

import java.io.Serializable;

/**
 * User: Marv
 * Date: 11/7/12
 */

public class TransmittalLetter implements Serializable {
    
    private String id;
    
    private TransmittalLetterCode transmittalLetterCode;
    
    private String letterDescription;
    
    private LetterType letterType;
    
    private String originalCopy;
    
    private String duplicateCopy;
    
    private int sequenceNumber;

	public TransmittalLetter() {}
    
    public TransmittalLetter(TransmittalLetterCode transmittalLetterCode, 
                             String letterDescription, 
                             LetterType letterType,
                             String originalCopy,
                             String duplicateCopy) {
        this.transmittalLetterCode = transmittalLetterCode;
        this.letterDescription = letterDescription;
        this.letterType = letterType;

        this.originalCopy = originalCopy;
        this.duplicateCopy = duplicateCopy;
    }

    public TransmittalLetter(TransmittalLetterCode transmittalLetterCode, 
    		String letterDescription, 
    		LetterType letterType,
    		String originalCopy,
    		String duplicateCopy,
    		int sequenceNumber) {
    	this.transmittalLetterCode = transmittalLetterCode;
    	this.letterDescription = letterDescription;
    	this.letterType = letterType;
    	
    	this.originalCopy = originalCopy;
    	this.duplicateCopy = duplicateCopy;
    	this.sequenceNumber = sequenceNumber;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
}
