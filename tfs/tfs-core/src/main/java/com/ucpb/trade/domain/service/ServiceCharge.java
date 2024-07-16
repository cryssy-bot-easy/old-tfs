package com.ucpb.trade.domain.service;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

/**
 * User: Jett
 * Date: 7/12/12
 * @author Jett Gamboa
 */
public class ServiceCharge {

    ChargeId chargeId;

    Money amount;



    // this is the default amount computed using the default charge formula
    Money defaultAmount;

    public ServiceCharge() {
//        amount = Money.of(CurrencyUnit.getInstance("PHP"), 100);
    }

}
