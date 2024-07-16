package com.ucpb.tfs.interfaces.gateway;

import java.math.BigDecimal;
import java.util.Currency;

/**
 */
public class ReversalRequest extends CasaRequest {


    private BigDecimal amount;

    private String workTaskId;


    public ReversalRequest(){

    }

    public ReversalRequest(FinRequest requestToReverse,String workTaskId){
        this.workTaskId = workTaskId;
        this.setAccountNumber(requestToReverse.getAccountNumber());
        this.setAmount(requestToReverse.getAmount());
        this.setBranchCode(requestToReverse.getBranchCode());
        this.setUserId(requestToReverse.getUserId());
    }


    @Override
    protected String doBuild() {
        System.out.println("\n\nMESSAGE STRING (ReversalRequest):");
        // ORIGINAL FORMAT
//        return String.format("%1$-8s%2$-8s%3$3s%4$4s%5$4s%6$12s%7$15s%8$8s    ",
//                getUsername(),getPassword(),getBranchCode(),getUserId(),
//                getTransactionCode().getCode(),getAccountNumber(),getAmountString(),getWorkTaskId());

        // MARV: added currency code for message string
        System.out.println("[START]  "+String.format("%1$-8s%2$-8s%3$3s%4$4s%5$4s%6$12s%7$15s%8$8s%9$3s ",
                getUsername(),getPassword(),getBranchCode(),getUserId(),
                getTransactionCode().getCode(),getAccountNumber(),getAmountString(),getWorkTaskId(),getCurrency().toString())+"[END]\n\n");
        return String.format("%1$-8s%2$-8s%3$3s%4$4s%5$4s%6$12s%7$15s%8$8s%9$3s ",
                getUsername(),getPassword(),getBranchCode(),getUserId(),
                getTransactionCode().getCode(),getAccountNumber(),getAmountString(),getWorkTaskId(),getCurrency().toString());

        // MARV: set teller id to pass the full 7 digit.
//        System.out.println("[START]  "+String.format("%1$-8s%2$-8s%3$3s%4$7s%5$4s%6$12s%7$15s%8$8s%9$3s ",
//                getUsername(),getPassword(),getBranchCode(),getUserId(),
//                getTransactionCode().getCode(),getAccountNumber(),getAmountString(),getWorkTaskId(),getCurrency().toString())+"[END]\n\n");
//        return String.format("%1$-8s%2$-8s%3$3s%4$7s%5$4s%6$12s%7$15s%8$8s%9$3s ",
//                getUsername(),getPassword(),getBranchCode(),getUserId(),
//                getTransactionCode().getCode(),getAccountNumber(),getAmountString(),getWorkTaskId(),getCurrency().toString());
    }

    public String getWorkTaskId() {
        return workTaskId;
    }

    public void setWorkTaskId(String workTaskId) {
        this.workTaskId = workTaskId;
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
    public String toString(){
        return "REQUEST:'" + toRequestString()+ "'";
    }

}
