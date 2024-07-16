package com.ucpb.tfs.domain.product;

import com.ucpb.tfs.utils.UtilSetFields;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 3/5/13
 * Time: 6:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class NonLcDetails {

    private String tenor; // DA, DP, OA, DR

    private Long tenorTerm;
    
    private String nonLcTenor;
    private String nonLcTenorTerm;

    private Currency draftCurrency;

    private BigDecimal draftAmount;

    private Date dueDate;

    private String collectingBankCode;
    private String collectingBankAddress;

    private String nonLcDescriptionOfGoods;

    public NonLcDetails() {}

    public NonLcDetails(Map<String, Object> details) {
        UtilSetFields.copyMapToObject(this, (HashMap) details);

        if (details.get("draftCurrency") != null) {
            this.draftCurrency = Currency.getInstance((String) details.get("draftCurrency"));
        }

        if (details.get("draftAmount") != null) {
            this.draftAmount = new BigDecimal((String) details.get("draftAmount"));
        }
    }

    public Date getDueDate() {
        return dueDate;
    }
}
