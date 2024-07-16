package com.ucpb.tfs.domain.accounting.enumTypes;

/**
 * User: giancarlo
 * Date: 10/12/12
 * Time: 5:31 PM
 */
public enum BookCurrency {

//    PHP,USD,THIRD

    PHP("PHP"), USD("USD"),THIRD("THIRD");

    private final String code;

    BookCurrency(String code) {
        System.out.println(code);
        this.code = code;
    }
}
