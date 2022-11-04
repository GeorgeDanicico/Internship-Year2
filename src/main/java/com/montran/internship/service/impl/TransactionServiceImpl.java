package com.montran.internship.service.impl;

import com.montran.internship.model.Account;
import com.montran.internship.model.Consent;
import com.montran.internship.model.Token;
import com.montran.internship.model.Transaction;
import com.montran.internship.model.enums.Banks;
import com.montran.internship.payload.dto.TokenDTO;
import com.montran.internship.payload.dto.TransactionsDTO;
import com.montran.internship.repository.AccountRepository;
import com.montran.internship.repository.TokenRepository;
import com.montran.internship.repository.TransactionRepository;
import com.montran.internship.service.TransactionService;
import com.montran.internship.service.bankcalls.BRDBankCall;
import com.montran.internship.service.bankcalls.BTBankCall;
import com.montran.internship.service.bankcalls.CecBankCall;
import com.montran.internship.service.oauth.AuthorizationService;
import com.montran.internship.util.BTUtil;
import com.montran.internship.util.CECUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BTBankCall btBankCall;
    @Autowired
    private BRDBankCall brdBankCall;
    @Autowired
    private CecBankCall cecBankCall;
    @Autowired
    private AuthorizationService authorizationService;

    private void saveTransactions(List<Transaction> transactions) {
        if (transactions != null) {
            for (Transaction tr : transactions) {
                Transaction transaction = transactionRepository.findByTransactionIdAndAccount(tr.getTransactionId(),
                        tr.getAccount()).orElse(tr);
                transactionRepository.save(transaction);
            }
        }
    }

    private TransactionsDTO getBtAccountTransactions(Consent consent, String accessToken, String accountId,
                                                     String dateFrom, String dateTo) {
        Account account = accountRepository.findByResourceIdAndProfile(accountId, consent.getProfile()).orElse(null);
        TokenDTO tokenDTO = null;
        if (account == null) {
            return null;
        }

        List<Transaction> transactions = btBankCall.fetchTransactions(consent.getConsentId(), accessToken, account, dateFrom, dateTo);

        if (btBankCall.getHasExpired() || transactions == null) {
            Token token = account.getToken();
            btBankCall.setHasExpired(false);
            tokenDTO = authorizationService.oauth(consent, BTUtil.authRedirect(), token.getRefreshToken(), true);
            transactions = btBankCall.fetchTransactions(consent.getConsentId(), tokenDTO.getAccessToken(), account, dateFrom, dateTo);
        }

        this.saveTransactions(transactions);

        return new TransactionsDTO(transactions, tokenDTO);
    }

    private TransactionsDTO getBRDAccountTransactions(Consent consent, String accountId,
                                                     String dateFrom, String dateTo) {
        Account account = accountRepository.findByResourceIdAndProfile(accountId, consent.getProfile()).orElse(null);
        List<Transaction> transactions = brdBankCall.fetchTransactions(consent.getConsentId(), account, dateFrom, dateTo);

        this.saveTransactions(transactions);

        return new TransactionsDTO(transactions);
    }

    private TransactionsDTO getCecAccountTransactions(Consent consent, String accessToken, String accountId,
                                                     String dateFrom, String dateTo) {
        Account account = accountRepository.findByResourceIdAndProfile(accountId, consent.getProfile()).orElse(null);
        TokenDTO tokenDTO = null;
        if (account == null) {
            return null;
        }

        List<Transaction> transactions = cecBankCall.fetchTransactions(consent, accessToken, account, dateFrom, dateTo);

        if (cecBankCall.getHasExpired() || transactions == null) {
            Token token = account.getToken();
            cecBankCall.setHasExpired(false);
            tokenDTO = authorizationService.oauth(consent, CECUtil.authRedirect(), token.getRefreshToken(), true);
            transactions = cecBankCall.fetchTransactions(consent, tokenDTO.getAccessToken(), account, dateFrom, dateTo);
        }

        this.saveTransactions(transactions);

        return new TransactionsDTO(transactions, tokenDTO);
    }

    @Override
    public TransactionsDTO getAccountTransactions(Consent consent, String accessToken, String accountId,
                                                  String dateFrom, String dateTo) {
        if (consent == null) {
            return new TransactionsDTO(new ArrayList<>());
        }

        if (consent.getBank().getBankName().equals(Banks.BT.label)) {
            return this.getBtAccountTransactions(consent, accessToken, accountId,
                    dateFrom, dateTo);
        }

        if (consent.getBank().getBankName().equals(Banks.BRD.label)) {
            return this.getBRDAccountTransactions(consent, accountId,
                    dateFrom, dateTo);
        }

        if (consent.getBank().getBankName().equals(Banks.CEC.label)) {
            return this.getCecAccountTransactions(consent, accessToken, accountId,
                    dateFrom, dateTo);
        }

        return null;
    }
}
