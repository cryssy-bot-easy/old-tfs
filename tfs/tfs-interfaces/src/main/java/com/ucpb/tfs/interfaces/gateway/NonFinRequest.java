package com.ucpb.tfs.interfaces.gateway;

/**
 */
public class NonFinRequest extends CasaRequest {


    @Override
    protected String doBuild() {
        System.out.println("\n\nMESSAGE STRING (NonFinRequest):");
        // ORIGINAL FORMAT
//        return String.format("%1$-8s%2$-8s%3$3s%4$4s%5$4s%6$12s",
//                getUsername(),getPassword(),getBranchCode(),getUserId(),
//                getTransactionCode().getCode(),getAccountNumber());

        // MARV: added currency code for message string
        System.out.println("[START]"+String.format("%1$-8s%2$-8s%3$3s%4$4s%5$4s%6$12s                       %7$3s ",
                getUsername(),getPassword(),getBranchCode(),getUserId(),
                getTransactionCode().getCode(),getAccountNumber(),getCurrency().toString())+"[END]\n\n");
        return String.format("%1$-8s%2$-8s%3$3s" +
                "%4$4s%5$4s%6$12s                       %7$3s ",
                getUsername(),getPassword(),getBranchCode(),getUserId(),
                getTransactionCode().getCode(),getAccountNumber(),getCurrency().toString());

        // MARV: set teller id to pass the full 7 digit.
//        System.out.println("[START]"+String.format("%1$-8s%2$-8s%3$3s%4$7s%5$4s%6$12s                       %7$3s ",
//                getUsername(),getPassword(),getBranchCode(),getUserId(),
//                getTransactionCode().getCode(),getAccountNumber(),getCurrency().toString())+"[END]\n\n");
//        return String.format("%1$-8s%2$-8s%3$3s%4$7s%5$4s%6$12s                       %7$3s ",
//                getUsername(),getPassword(),getBranchCode(),getUserId(),
//                getTransactionCode().getCode(),getAccountNumber(),getCurrency().toString());
    }
}