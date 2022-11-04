package com.montran.internship.payload.dto;

import com.montran.internship.model.Transaction;

import java.util.List;

public class TransactionsDTO {
    private List<Transaction> transactions;
    private TokenDTO tokenDTO;

    public TransactionsDTO(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public TransactionsDTO(List<Transaction> transactions, TokenDTO tokenDTO) {
        this.transactions = transactions;
        this.tokenDTO = tokenDTO;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public TokenDTO getTokenDTO() {
        return tokenDTO;
    }

    public void setTokenDTO(TokenDTO tokenDTO) {
        this.tokenDTO = tokenDTO;
    }
}
