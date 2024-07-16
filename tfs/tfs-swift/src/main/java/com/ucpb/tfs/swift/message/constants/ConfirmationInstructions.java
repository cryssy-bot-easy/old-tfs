package com.ucpb.tfs.swift.message.constants;

/**
 */
public enum ConfirmationInstructions {

    YES("Y","CONFIRM"),NO("N","WITHOUT"),MAY_ADD("M","MAY ADD"),DEFAULT("","");

    private String alias;
    private String swiftCode;

    private ConfirmationInstructions(String alias,String swiftCode){
        this.alias = alias;
        this.swiftCode = swiftCode;
    }

    public String getSwiftCode(){
        return swiftCode;
    }

    public String getAlias(){
        return  alias;
    }

    public static ConfirmationInstructions getValue(String code){
        for(ConfirmationInstructions instruction : ConfirmationInstructions.values()){
            if(instruction.getAlias().equalsIgnoreCase(code)){
                return instruction;
            }
        }
        return DEFAULT;
    }

}
