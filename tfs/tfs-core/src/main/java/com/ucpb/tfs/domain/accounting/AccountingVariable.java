package com.ucpb.tfs.domain.accounting;

import com.ucpb.tfs.domain.accounting.enumTypes.AccountingEntryType;
import com.ucpb.tfs.domain.accounting.enumTypes.BookCode;
import com.ucpb.tfs.domain.accounting.enumTypes.BookCurrency;
import com.ucpb.tfs.domain.accounting.enumTypes.EntryType;
import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.utils.CalculatorUtils;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * User: giancarlo
 * Date: 9/29/12
 * Time: 3:27 PM
 */
public class AccountingVariable implements Serializable {

    private long id;

    //Properties that define it
    String code;


    public AccountingVariable() {
    }

    public AccountingVariable(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "AccountingVariable{" +
                "id=" + id +
                ", code='" + code + '\'' +
                '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
