package com.ucpb.tfs2.application.service;

import org.springframework.security.core.AuthenticationException;

public class CustomAdAuthenticationException extends AuthenticationException {

    private final String dataCode;

    CustomAdAuthenticationException(String dataCode, String message, Throwable cause) {
        super(message, cause);
        this.dataCode = dataCode;
    }

    public String getDataCode() {
        return dataCode;
    }
}
