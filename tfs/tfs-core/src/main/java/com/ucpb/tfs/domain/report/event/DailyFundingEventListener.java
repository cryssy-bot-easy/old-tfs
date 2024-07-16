package com.ucpb.tfs.domain.report.event;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Component;
import com.incuventure.ddd.infrastructure.events.EventListener;

import com.ucpb.tfs.domain.product.event.DASettlementCreatedEvent;
import com.ucpb.tfs.domain.product.event.DPSettlementCreatedEvent;
import com.ucpb.tfs.domain.product.event.DRSettlementCreatedEvent;
import com.ucpb.tfs.domain.product.event.ImportAdvancePaymentCreatedEvent;
import com.ucpb.tfs.domain.product.event.ImportAdvancePaymentRefundCreatedEvent;
import com.ucpb.tfs.domain.product.event.OASettlementCreatedEvent;
import com.ucpb.tfs.domain.report.DailyFunding;
import com.ucpb.tfs.domain.report.DailyFundingRepository;
import com.ucpb.tfs.domain.report.enums.BankType;
import com.ucpb.tfs.domain.service.TradeService;

import javax.inject.Inject;

@Component
public class DailyFundingEventListener {
	
	@Inject
	DailyFundingRepository dailyFundingRepository;

	@EventListener
	public void createDailyFundingDa(DASettlementCreatedEvent daSettlementCreatedEvent){
		System.out.println("Logging Payment for Document Against Acceptance");
		TradeService tradeService = daSettlementCreatedEvent.getTradeService();
		createDailyFundingNonLc(tradeService);
	}
	
	@EventListener
	public void createDailyFundingDp(DPSettlementCreatedEvent dpSettlementCreatedEvent){
		System.out.println("Logging Payment for Document Against Payment");
		TradeService tradeService = dpSettlementCreatedEvent.getTradeService();
		createDailyFundingNonLc(tradeService);
	}
	
	@EventListener
	public void createDailyFundingOa(OASettlementCreatedEvent oaSettlementCreatedEvent){
		System.out.println("Logging Payment for Open Account");
		TradeService tradeService = oaSettlementCreatedEvent.getTradeService();
		createDailyFundingNonLc(tradeService);
	}
	
	@EventListener
	public void createDailyFundingDr(DRSettlementCreatedEvent drSettlementCreatedEvent){
		System.out.println("Logging Payment for Direct Remittance");
		TradeService tradeService = drSettlementCreatedEvent.getTradeService();
		createDailyFundingNonLc(tradeService);
	}
	
	@EventListener
	public void createDailyFundingImportAdvance(ImportAdvancePaymentCreatedEvent importAdvancePaymentCreatedEvent){
		System.out.println("Logging Payment for Import Advance Payment");
		TradeService tradeService = importAdvancePaymentCreatedEvent.getTradeService();
		createDailyFundingImportAdvance(tradeService);
	}
	
	@EventListener
	public void createDailyFundingImportAdvance(ImportAdvancePaymentRefundCreatedEvent importAdvancePaymentRefundCreatedEvent){
		System.out.println("Logging Payment for Import Advance Payment Refund");
		TradeService tradeService = importAdvancePaymentRefundCreatedEvent.getTradeService();
		createDailyFundingImportAdvance(tradeService);
	}
	
	
	public void createDailyFundingNonLc(TradeService tradeService) {

		System.out.println("parameters for new instance of Daily Funding:");
		System.out.println("TradeserviceId: "
				+ tradeService.getTradeServiceId());
		System.out
				.println("ReimbursingBank: "
						+ (String) tradeService.getDetails().get(
								"reimbursingBank"));
		System.out
		.println("ReimbursingBankName: "
				+ (String) tradeService.getDetails().get(
						"reimbursingBankName"));
		System.out.println("Currency: "
				+ (String) tradeService.getDetails().get("currency"));
		System.out.println("amount: "
				+ tradeService.getDetails().get("productAmount").toString());
		
		
		DailyFunding dailyFunding = new DailyFunding(
				tradeService.getTradeServiceId(), (String) tradeService
						.getDetails().get("reimbursingBank"),
				(String) tradeService.getDetails().get("reimbursingBankName"),
				BankType.REIMBURSING, (String) tradeService.getDetails().get(
						"currency"), new BigDecimal(tradeService.getDetails()
						.get("productAmount").toString().replace(",","")), new Date());
		
		System.out.println("Created new instance of Daily Funding");
		dailyFundingRepository.persist(dailyFunding);
		
	}
	
	public void createDailyFundingImportAdvance(TradeService tradeService) {
		System.out.println("parameters for new instance of Daily Funding:");
		System.out.println("TradeserviceId: "
				+ tradeService.getTradeServiceId());
		System.out
		.println("ReimbursingBank: "
				+ (String) tradeService.getDetails().get(
						"reimbursingBankCode"));
		System.out
		.println("ReimbursingBankName: "
				+ (String) tradeService.getDetails().get(
						"reimbursingBankName"));
		System.out.println("Currency: "
				+ (String) tradeService.getDetails().get("currency"));
		System.out.println("amount: "
				+ tradeService.getDetails().get("amount").toString());
		
		
		DailyFunding dailyFunding = new DailyFunding(
				tradeService.getTradeServiceId(), (String) tradeService
				.getDetails().get("reimbursingBankCode"),
				(String) tradeService.getDetails().get("reimbursingBankName"),
				BankType.REIMBURSING, (String) tradeService.getDetails().get(
						"currency"), new BigDecimal(tradeService.getDetails()
								.get("amount").toString().replace(",","")), new Date());
		
		System.out.println("Created new instance of Daily Funding");
		dailyFundingRepository.persist(dailyFunding);
		
	}
}
