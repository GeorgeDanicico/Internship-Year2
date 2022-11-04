package com.montran.internship.payload.dto;

import java.util.List;

public class AccountsDTO {
    private List<AccountDTO> accounts;
    private TokenDTO accessToken;

    public AccountsDTO(List<AccountDTO> accounts) {
        this.accounts = accounts;
    }

    public AccountsDTO(List<AccountDTO> accounts, TokenDTO btAccessToken) {
        this.accounts = accounts;
        this.accessToken = btAccessToken;
    }

    public List<AccountDTO> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountDTO> accounts) {
        this.accounts = accounts;
    }

    public TokenDTO getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(TokenDTO accessToken) {
        this.accessToken = accessToken;
    }
}
