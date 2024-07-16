package com.ucpb.trade.domain.service;

import org.joda.money.Money;

/**
 * User: Jett
 * Date: 7/12/12
 * @author Jett Gamboa
 */
public class ProductCharge {

    Money amount;

    // this is the amount before it is overridden
    // todo: validate if product charge can be overridden (most likely not)
    Money defaultAmount;

}
