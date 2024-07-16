package com.ucpb.tfs.domain.cdt;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 1/13/14
 * Time: 10:21 AM
 * To change this template use File | Settings | File Templates.
 */


public class CDTPaymentHistory implements Serializable {

    String id;

    String iedieirdNumber;

    Date dateUploaded;

    BigDecimal amount;

    String unitCode;

    public CDTPaymentHistory() { }

    public CDTPaymentHistory(String iedieirdNumber, Date dateUploaded, BigDecimal amount, String unitCode) {
        this.iedieirdNumber = iedieirdNumber;
        this.dateUploaded = dateUploaded;
        this.amount = amount;
        this.unitCode = unitCode;
    }

    public void updateAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
