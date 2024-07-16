package com.ucpb.tfs.batch.report.dw;

/**
 */
public enum DocumentClass {

    LC("LC","1"),DP("DP","2"),DA("DA","3"),OA("OA","4"),DR("DR","5"),OTHERS("OTHERS","9"),BC("BC","10"),INDEMNITY("INDEMNITY","11"),BP("BP","12"),
    EXPORT_ADVANCE("EXPORT_ADVANCE","13"),IMPORT_ADVANCE("IMPORT_ADVANCE","14"),EXPORT_ADVISING("EXPORT_ADVISING","15");


    private String modeOfPayment;
    private String documentClass;

    private DocumentClass(String documentClass,String modeOfPayment){
        this.documentClass = documentClass;
        this.modeOfPayment = modeOfPayment;
    }

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public String getDocumentClass() {
        return documentClass;
    }

    public static DocumentClass getDocumentClassByName(String name){
        for(DocumentClass documentClass : DocumentClass.values()){
            if(documentClass.getDocumentClass().equalsIgnoreCase(name)){
                return documentClass;
            }
        }
        return OTHERS;
    }

    public static DocumentClass getDocumentClassByPaymentMode(String paymentMode){
        for(DocumentClass documentClass : DocumentClass.values()){
            if(documentClass.getModeOfPayment().equalsIgnoreCase(paymentMode)){
                return  documentClass;
            }
        }
        return OTHERS;
    }

}
