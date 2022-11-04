package com.montran.internship.service;

import com.montran.internship.model.*;
import com.montran.internship.payload.dto.AccountsDTO;
import com.montran.internship.payload.response.MessageResponse;

import java.util.List;
import java.util.Map;

public interface AccountService {
    void saveAccounts(Consent consent, String accessToken, String refreshToken);
    void saveAccount(Consent consent, Account account, Token token);
    AccountsDTO getAccounts(Consent consent, String accessToken);
    AccountsDTO getAccountsForPayments(Profile profile);
    MessageResponse deleteAccount(Profile profile, String accountId);
}
