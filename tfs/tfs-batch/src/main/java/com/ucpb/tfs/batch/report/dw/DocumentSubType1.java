package com.ucpb.tfs.batch.report.dw;

public enum DocumentSubType1 {
    REGULAR("25"), CASH("0"), STANDBY("20"), DEFFERED("10"), REVOLVING("0"),FIRST_ADVISING("0"),SECOND_ADVISING("0"),
    DEFAULT("0");

    private String contingentType;

    private DocumentSubType1(String contingentType){
        this.contingentType = contingentType;
    }

    public String getContingentType() {
        return contingentType;
    }

    public static DocumentSubType1 getDocumentSubType1(String documentSubType1){
        for(DocumentSubType1 document : DocumentSubType1.values()){
            if(document.toString().equalsIgnoreCase(documentSubType1)){
                return document;
            }
        }
        return DEFAULT;
    }

}
