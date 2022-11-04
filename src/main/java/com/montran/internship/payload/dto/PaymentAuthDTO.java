package com.montran.internship.payload.dto;

public class PaymentAuthDTO {
    private String paymentId;
    private String redirectLink;
    private String status;

    public PaymentAuthDTO(String paymentId, String redirectLink) {
        this.paymentId = paymentId;
        this.redirectLink = redirectLink;
    }

    public PaymentAuthDTO(String paymentId, String redirectLink, String status) {
        this.paymentId = paymentId;
        this.redirectLink = redirectLink;
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getRedirectLink() {
        return redirectLink;
    }

    public void setRedirectLink(String redirectLink) {
        this.redirectLink = redirectLink;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
