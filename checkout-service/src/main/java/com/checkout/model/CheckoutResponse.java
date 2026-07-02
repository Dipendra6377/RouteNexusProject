package com.checkout.model;

public class CheckoutResponse {

    private String version;
    private String message;

    public CheckoutResponse() {
    }

    public CheckoutResponse(String version, String message) {
        this.version = version;
        this.message = message;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}