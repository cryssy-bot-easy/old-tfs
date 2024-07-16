package com.ucpb.tfs.interfaces.gateway;

import java.math.BigDecimal;
import java.util.Currency;

/**
 */
public class FinRequest extends CasaRequest {

    private BigDecimal amount;

    @Override
    protected String doBuild() {
        System.out.println("\n\nMESSAGE STRING (FinRequest):");
        // ORIGINAL FORMAT
//        return String.format("%1$-8s%2$-8s%3$3s%4$4s%5$4s%6$12s%7$15s            ",
//                getUsername(),getPassword(),getBranchCode(),getUserId(),
//                getTransactionCode().getCode(),getAccountNumber(),getAmountString());

        // MARV: added currency code for message string
        System.out.println("[START]"+String.format("%1$-8s%2$-8s%3$3s%4$4s%5$4s%6$12s%7$15s        %8$3s ",
                getUsername(), getPassword(), getBranchCode(), getUserId(),
                getTransactionCode().getCode(), getAccountNumber(), getAmountString(), getCurrency().toString())+"[END]\n\n");

        return String.format("%1$-8s%2$-8s%3$3s%4$4s%5$4s%6$12s%7$15s        %8$3s ",
                getUsername(), getPassword(), getBranchCode(), getUserId(),
                getTransactionCode().getCode(), getAccountNumber(), getAmountString(), getCurrency().toString());

        // MARV: set teller id to pass the full 7 digit.
//        System.out.println("[START]"+String.format("%1$-8s%2$-8s%3$3s%4$7s%5$4s%6$12s%7$15s        %8$3s ",
//                getUsername(), getPassword(), getBranchCode(), getUserId(),
//                getTransactionCode().getCode(), getAccountNumber(), getAmountString(), getCurrency().toString())+"[END]\n\n");
//
//        return String.format("%1$-8s%2$-8s%3$3s%4$7s%5$4s%6$12s%7$15s        %8$3s ",
//                getUsername(), getPassword(), getBranchCode(), getUserId(),
//                getTransactionCode().getCode(), getAccountNumber(), getAmountString(), getCurrency().toString());
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getAmountString(){
        return String.format("%1$015.2f",amount).replaceAll("\\.","");
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return toRequestString();
    }


}
