package com.ucpb.tfs.domain.accounting.enumTypes;

/**
 * Created with IntelliJ IDEA.
 * User: JAVA_training
 * Date: 9/29/12
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
public enum BookCode {

    RG("Regular"), FC("Foreign Currency");

    private final String code;

    BookCode(String code) {
        this.code = code;
    }
}
