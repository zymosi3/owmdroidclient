package com.zymosi3.owmdroidclient;

/**
 *
 */
public class OwmClientException extends RuntimeException {

    public OwmClientException(String detailMessage) {
        super(detailMessage);
    }

    public OwmClientException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
