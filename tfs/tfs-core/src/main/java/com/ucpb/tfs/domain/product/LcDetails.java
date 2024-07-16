package com.ucpb.tfs.domain.product;

import com.ucpb.tfs.domain.product.enums.LCTenor;
import com.ucpb.tfs.domain.product.enums.LCType;
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
public class LcDetails {

    private String adviseNumber;
    private DocumentNumber lcNumber;
    private Date lcIssueDate;
    private LCType lcType;
    private LCTenor lcTenor;
    private String usanceTerm;

    private Currency lcCurrency;
    private BigDecimal lcAmount;

    private Date lcExpiryDate;

    private String issuingBankCode;
    private String issuingBankAddress;

    private String reimbursingBankCode;
    private String lcDescriptionOfGoods;

    public LcDetails() {}

    public LcDetails(final Map<String, Object> details) {
        UtilSetFields.copyMapToObject(this, (HashMap) details);

        if (details.get("lcNumber") != null) {
            this.lcNumber = new DocumentNumber((String) details.get("lcNumber"));
        }

        if (details.get("lcCurrency") != null) {
            this.lcCurrency = Currency.getInstance((String) details.get("lcCurrency"));
        }

        if (details.get("lcAmount") != null) {
            this.lcAmount = new BigDecimal((String) details.get("lcAmount"));
        }

        if ("USANCE".equals((String) details.get("lcTenor")) && details.get("usanceTerm") != null) {
            this.usanceTerm = (String) details.get("usanceTerm");
        }
    }

    public Date getLcIssueDate() {
        return lcIssueDate;
    }

    public Date getLcExpiryDate() {
        return lcExpiryDate;
    }
}
