package com.ucpb.tfs.domain.product;

import com.ucpb.tfs.domain.product.enums.ProductType;
import com.ucpb.tfs.utils.UtilSetFields;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 4/11/13
 * Time: 10:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class Rebate {

    private String id;

    private DocumentNumber documentNumber;
    private String cifNumber;
    private String cifName;

    private String accountOfficer;
    private String ccbdBranchUnitCode;

    private Currency currency;
    private BigDecimal amount;

    private Date processDate;
    private String corresBankCode;
    private String accountType;
    private String depositoryAccountNumber;
    private String glCode;
    private String countryCode;
    private String beneficiary;
    private String beneficiaryTin;
    private String particulars;

    private String tranCode;

    public Rebate() {}

    public Rebate(DocumentNumber documentNumber, Map<String, Object> details) {
        this.documentNumber = documentNumber;

        this.amount = new BigDecimal((String) details.get("amount"));
        this.currency = Currency.getInstance((String) details.get("currency"));

        UtilSetFields.copyMapToObject(this, (HashMap) details);
    }

    public Rebate(Map<String, Object> details) {
        this.amount = new BigDecimal((String) details.get("amount"));
        this.currency = Currency.getInstance((String) details.get("currency"));

        UtilSetFields.copyMapToObject(this, (HashMap) details);
    }

}
