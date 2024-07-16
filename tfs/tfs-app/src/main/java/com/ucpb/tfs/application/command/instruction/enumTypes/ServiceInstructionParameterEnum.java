/**
 * 
 */
package com.ucpb.tfs.application.command.instruction.enumTypes;

/**
 * @author Val
 *
 */
public enum ServiceInstructionParameterEnum {

	USER_ID,
	DATE_APPROVED,
	TYPE,
	SERVICE_INSTRUCTION_NUMBER,
	DOCUMENT_NUMBER,
	STATUS,
	DETAILS,
	ROUTES;

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		String str = "";
		switch(this) {
			case USER_ID:
				str = "userId";
				break;
			case DATE_APPROVED:
				str = "dateApproved";
				break;
			case TYPE:
				str = "type";
				break;
			case SERVICE_INSTRUCTION_NUMBER:
				str = "serviceInstructionNumber";
				break;
			case DOCUMENT_NUMBER:
				str = "documentNumber";
				break;
			case STATUS:
				str = "status";
				break;
			case DETAILS:
				str = "details";
				break;
			case ROUTES:
				str = "routes";
				break;
		}
		return str;
	}
}
