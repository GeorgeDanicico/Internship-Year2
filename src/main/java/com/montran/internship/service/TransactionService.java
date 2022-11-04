package com.montran.internship.service;

import com.montran.internship.model.Consent;
import com.montran.internship.payload.dto.TransactionsDTO;

public interface TransactionService {
    TransactionsDTO getAccountTransactions(Consent consent, String accessToken,
                                           String accountId, String dateFrom, String dateTo);
}
