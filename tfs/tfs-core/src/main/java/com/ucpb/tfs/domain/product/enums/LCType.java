package com.ucpb.tfs.domain.product.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Geek
 * Date: 6/2/12
 * Time: 12:33 AM
 * To change this template use File | Settings | File Templates.
 */
public enum LCType {
    REGULAR("25"), CASH("0"), STANDBY("20"), DEFFERED("10"), REVOLVING("0");
    
    private String contingentType;
    
    private LCType(String contingentType){
    	this.contingentType = contingentType;
    }

	public String getContingentType() {
		return contingentType;
	}

}
