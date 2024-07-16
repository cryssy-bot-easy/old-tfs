package com.ucpb.tfs.domain.email;

/**
 * Custom exception for Email Exception
 */

public class EmailException extends Exception {

    public EmailException() {
        super("Email Sending failed!");
    }

    public EmailException(String message) {
        super(message);
    }

    public EmailException(Throwable cause) {
        super(cause);
    }

    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
