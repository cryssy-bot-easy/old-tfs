package com.ucpb.tfs.domain.sysparams;

public class RefTransmittalLetter {
	
	private Long id;
	
	private String transmittalLetterCode;
	
	private String letterDescription;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTransmittalLetterCode() {
		return transmittalLetterCode;
	}

	public void setTransmittalLetterCode(String transmittalLetterCode) {
		this.transmittalLetterCode = transmittalLetterCode;
	}

	public String getLetterDescription() {
		return letterDescription;
	}

	public void setLetterDescription(String letterDescription) {
		this.letterDescription = letterDescription;
	}
	
	
}
