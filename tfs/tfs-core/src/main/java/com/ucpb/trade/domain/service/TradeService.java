package com.ucpb.trade.domain.service;

import com.incuventure.ddd.domain.annotations.DomainAggregateRoot;
import com.ucpb.trade.domain.product.DocumentNumber;
import com.ucpb.trade.domain.product.TradeProduct;
import org.joda.money.CurrencyUnit;

import java.util.List;

/**
 * User: Jett
 * Date: 7/12/12
 * @author Jett Gamboa
  */

@DomainAggregateRoot
public class TradeService {

    Long id;

    DocumentNumber documentNumber;

    // working copy of product
    TradeProduct workingCopy;

    // reference to JBPM process instance
    Long processId;

    // currency that charges will be paid in
    // from requirement that all charges are paid using one currency only
    CurrencyUnit chargesCurrency;
    List<ServiceCharge> serviceCharges;

    List<ProductCharge> productCharges;


    public void Service(DocumentNumber documentNumber) {

        this.documentNumber = documentNumber;

    }

    public void addCharge(ServiceCharge charge) {

    }

    public void addProductCharge(ProductCharge charge) {

    }
}
