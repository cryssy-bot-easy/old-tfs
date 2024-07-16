package com.ucpb.tfs.domain.sysparams;

public class RefInstructionToBank {
	
	private Long id;
	
	private String instructionToBankCode;
	
	private String instruction;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInstructionToBankCode() {
		return instructionToBankCode;
	}

	public void setInstructionToBankCode(String instructionToBankCode) {
		this.instructionToBankCode = instructionToBankCode;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	
	
}
