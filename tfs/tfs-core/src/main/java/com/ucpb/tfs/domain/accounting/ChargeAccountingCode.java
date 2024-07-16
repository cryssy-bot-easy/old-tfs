package com.ucpb.tfs.domain.accounting;

import com.ucpb.tfs.domain.reference.ChargeId;
import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.io.Serializable;

/**
 * User: angulo
 * Date: 4/8/13
 * Time: 4:48 PM
 */
public class ChargeAccountingCode implements Serializable{
    private long id;
    ProductId productId;
    ServiceType serviceType;
    String accountingCode;
    ChargeId chargeId;

    public ChargeAccountingCode(){

    }

    public ChargeAccountingCode(long id, ProductId productId, ServiceType serviceType, String accountingCode, ChargeId chargeId) {
        this.id = id;
        this.productId = productId;
        this.serviceType = serviceType;
        this.accountingCode = accountingCode;
        this.chargeId = chargeId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProductId getProductId() {
        return productId;
    }

    public void setProductId(ProductId productId) {
        this.productId = productId;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }

    public ChargeId getChargeId() {
        return chargeId;
    }

    public void setChargeId(ChargeId chargeId) {
        this.chargeId = chargeId;
    }
}
