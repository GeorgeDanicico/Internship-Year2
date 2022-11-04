package com.montran.internship.payload.dto;

public class AccountDTO {
    private String iban;
    private String accountId;
    private String currency;
    private Double amount;
    private String bank;

    public AccountDTO(String iban, String accountId, String currency, Double amount) {
        this.iban = iban;
        this.accountId = accountId;
        this.currency = currency;
        this.amount = amount;
    }

    public AccountDTO(String iban, String accountId, String currency, Double amount, String bank) {
        this.iban = iban;
        this.accountId = accountId;
        this.currency = currency;
        this.amount = amount;
        this.bank = bank;
    }


    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }
}
