package com.ucpb.tfs.domain.settlementaccount.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.product.TradeProduct;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.settlementaccount.MarginalDeposit;

import java.util.LinkedList;

/**
 */
public class MarginalDepositUpdatedEvent implements DomainEvent {

  private TradeService tradeService;
  private TradeProduct tradeProduct;
  private MarginalDeposit md;
  private LinkedList<LinkedList<Object>> linkedListMda;
  private String gltsNumber;

  public MarginalDepositUpdatedEvent(TradeService tradeService, TradeProduct tradeProduct, MarginalDeposit md, LinkedList<LinkedList<Object>> linkedListMda, String gltsNumber) {
      this.tradeService = tradeService;
      this.tradeProduct = tradeProduct;
      this.md = md;
      this.linkedListMda = linkedListMda;
      this.gltsNumber = gltsNumber;
  }

    public TradeService getTradeService() {
        return tradeService;
    }

    public TradeProduct getTradeProduct() {
        return tradeProduct;
    }

    public MarginalDeposit getMd() {
        return md;
    }

    public void setMd(MarginalDeposit md) {
        this.md = md;
    }

    public LinkedList<LinkedList<Object>> getLinkedListMda() {
        return linkedListMda;
    }

    public String getGltsNumber() {
        return gltsNumber;
    }
}
