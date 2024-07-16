package com.ucpb.tfs.batch.job.enums;

public enum UpdateCifNumberQueries {

	ACCOUNTS_PAYABLE("update ACCOUNTSPAYABLE x "+ 
				"set x.CIFNUMBER = (select y.CFNCIF from CIFNORM_CFNMSTA y where y.CFNCFO = CIFNUMBER AND y.CFNCFO IS NOT NULL) "+
				"where  exists (select 1 from CIFNORM_CFNMSTA y where y.CFNCFO = CIFNUMBER)"),
	ACCOUNTS_RECEIVABLE("update ACCOUNTSRECEIVABLE x "+ 
				"set x.CIFNUMBER = (select y.CFNCIF from CIFNORM_CFNMSTA y where y.CFNCFO = CIFNUMBER AND y.CFNCFO IS NOT NULL) " +
				"where  exists (select 1 from CIFNORM_CFNMSTA y where y.CFNCFO = CIFNUMBER)"),
	MARGINAL_DEPOSIT("update MARGINALDEPOSIT x "+
				"set x.CIFNUMBER = (select y.CFNCIF from CIFNORM_CFNMSTA y where y.CFNCFO = CIFNUMBER AND y.CFNCFO IS NOT NULL) "+
				"where  exists (select 1 from CIFNORM_CFNMSTA y where y.CFNCFO = CIFNUMBER)"),
			
	REBATE("update REBATE x "+ 
			"set x.CIFNUMBER = (select y.CFNCIF from CIFNORM_CFNMSTA y where y.CFNCFO = CIFNUMBER AND y.CFNCFO IS NOT NULL) "+
			"where  exists (select 1 from CIFNORM_CFNMSTA y where y.CFNCFO = CIFNUMBER)"),
	
	REFPAS5CLIENT("update REFPAS5CLIENT x "+
			"set x.CIFNO = (select y.CFNCIF from CIFNORM_CFNMSTA y where y.CFNCFO = CIFNO AND y.CFNCFO IS NOT NULL) "+
			"where  exists (select 1 from CIFNORM_CFNMSTA y where y.CFNCFO = CIFNO)"),

    CDTPAYMENTREQUEST("update CDTPAYMENTREQUEST x "+
            "set x.CIFNO = (select y.CFNCIF from CIFNORM_CFNMSTA y where y.CFNCFO = CIFNO AND y.CFNCFO IS NOT NULL) "+
            "where  exists (select 1 from CIFNORM_CFNMSTA y where y.CFNCFO = CIFNO)");
	
//	REF_TFCIFNOS("update REF_TFCIFNOS x "+ 
//			"set x.CIF_NO = (select y.CFNCIF from CIFNORM_CFNMSTA y where y.CFNCFO = CIF_NO AND y.CFNCFO IS NOT NULL) "+
//			"where  exists (select 1 from CIFNORM_CFNMSTA y where y.CFNCFO = CIF_NO)"),
//	
//	REF_TFCLNT("update REF_TFCLNT x "+
//			"set x.CIF_NO = (select y.CFNCIF from CIFNORM_CFNMSTA y where y.CFNCFO = CIF_NO AND y.CFNCFO IS NOT NULL) "+
//			"where  exists (select 1 from CIFNORM_CFNMSTA y where y.CFNCFO = CIF_NO)"),
//	
//	REF_TFCUSTMR("update REF_TFCUSTMR x "+ 
//			"set x.CBS_CIF_NO = (select y.CFNCIF from CIFNORM_CFNMSTA y where y.CFNCFO = CBS_CIF_NO AND y.CFNCFO IS NOT NULL) "+
//			"where  exists (select 1 from CIFNORM_CFNMSTA y where y.CFNCFO = CBS_CIF_NO)")
				
	private String query;
	
	private UpdateCifNumberQueries(String query){
		this.query=query;
	}
	
	@Override
	public String toString(){
		return this.query;
	}
}
