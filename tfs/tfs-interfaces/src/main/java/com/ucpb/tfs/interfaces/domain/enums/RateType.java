package com.ucpb.tfs.interfaces.domain.enums;

public enum RateType {

	BANK_NOTE_BUYING_RATE(1,"Bank Note Buying Rate"),
	BANK_NOTE_SELL(2,"Bank Note Sell/Invisibles"),
	URR(3,"Booking Rate"),
	EVALUATION(10,"Evaluation Money"),
	DEMAND_DRAFT_BUY(11,"Demand Draft Buy/EBP Exports Buy"),
	DEMAND_DRAFT_SELL(12,"Demand Draft Sell Rates"),
	COLL_TT_FCDU_BUYING(13,"COLL/TT/FCDU W/DRAWAL BUYING"),
	LC_REGULAR_SELL(14,"TT SELL/ LC Regular Sell"),
	DAILY_BASE_RATE(15,"Daily Base Rate"),
	EXPORTS_COLL_TT_BUY_RATE(16,"Exports Coll/TT Buy Rate"),
	LC_CASH_SELL_RATE(17,"LC Cash Sell Rate"),
	EOD_REVALUATION_RATE(18,"EOD Revaluation Rate"),
	EXPORTS_BUY_RATE(19,"Exports Buy Rate");
	
	private int rateNumber;
	
	private String description;
	
	private RateType(int rateNumber, String description){
		this.rateNumber = rateNumber;
		this.description = description;
	}
	
	public int getRateNumber(){
		return rateNumber;
	}
	
	public String getDescription(){
		return description;
	}
}
