package com.ucpb.tfs.domain.product.event;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.incuventure.ddd.infrastructure.events.EventListener;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.ExportBills;
import com.ucpb.tfs.domain.product.ExportBillsRepository;
import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceRepository;

import java.util.*;

@Component
public class ExportBillsEventListeners {

	@Autowired
    ExportBillsRepository exportBillsRepository;
	
	@Autowired
    TradeProductRepository tradeProductRepository;
	
	@Autowired
	TradeServiceRepository tradeServiceRepository;

    @Autowired
    PaymentRepository paymentRepository;
	
	@EventListener
    public void updateExportBills(ExportBillsPurchaseCreatedEvent exportBillsPurchaseCreatedEvent) {
		System.out.println("\nUpdating Bills for Collection");
		try {
		TradeService tradeService = tradeServiceRepository.load(exportBillsPurchaseCreatedEvent.getTradeServiceId());
		
		if (tradeService.getDetails().get("negotiationNumber") != null) {
        	//loads BC corresponding to Negotiation Number
            ExportBills exportBills = exportBillsRepository.load(new DocumentNumber(tradeService.getDetails().get("negotiationNumber").toString()));
            
            Map<String, Object> details = tradeService.getDetails();
            if (null != details.get("loanMaturityDate") && details.get("loanMaturityDate").toString() != ""){
            	details.put("loanMaturityDate", new SimpleDateFormat("MM/dd/yyyy").format(new Date(details.get("loanMaturityDate").toString())));
            }
            
            //updates BP details to BC
            Payment savedProductPayment = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);

            if (savedProductPayment != null) {
            	Set<PaymentDetail> savedPaymentDetails = savedProductPayment.getDetails();
            	for (PaymentDetail pd : savedPaymentDetails) {
                    if (pd.getStatus().equals(PaymentStatus.PAID)) {
                        if (pd.getPaymentInstrumentType().equals(PaymentInstrumentType.EBP)) {
                        	details.put("facilityType", pd.getFacilityType());
                        	details.put("facilityId", pd.getFacilityId().toString());
                        	details.put("loanAmount", pd.getAmount());
                        	details.put("agriAgraTagging", pd.getAgriAgraTagging());
                        	details.put("paymentCode", pd.getPaymentCode().toString());
                        	details.put("pnNumber", pd.getPnNumber().toString());
                        }
                    }
            	}
            }
            exportBills.setLoanDetails(details);
            
            exportBills.setBpDetailsForCollection(Currency.getInstance(tradeService.getDetails().get("currency").toString()), new BigDecimal(tradeService.getDetails().get("amount").toString()));
            tradeProductRepository.merge(exportBills);
            System.out.println("Export Bills for Collection Merged");
        }
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
