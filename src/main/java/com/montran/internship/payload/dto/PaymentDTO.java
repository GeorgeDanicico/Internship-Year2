package com.montran.internship.payload.dto;

import java.util.Date;

public class PaymentDTO {
    private String currency;
    private String creditorName;
    private Date date;
    private Double amount;
    private String paymentId;
    private String creditorIban;
    private String status;

    public PaymentDTO(String currency, String creditorName, Date date, Double amount, String paymentId, String creditorIban, String status) {
        this.currency = currency;
        this.creditorName = creditorName;
        this.date = date;
        this.amount = amount;
        this.paymentId = paymentId;
        this.creditorIban = creditorIban;
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public void setCreditorName(String creditorName) {
        this.creditorName = creditorName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCreditorIban() {
        return creditorIban;
    }

    public void setCreditorIban(String creditorIban) {
        this.creditorIban = creditorIban;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
