package com.montran.internship.service.impl;

import com.montran.internship.model.*;
import com.montran.internship.model.enums.Banks;
import com.montran.internship.payload.dto.AccountDTO;
import com.montran.internship.payload.dto.AccountsDTO;
import com.montran.internship.payload.dto.TokenDTO;
import com.montran.internship.payload.response.MessageResponse;
import com.montran.internship.repository.*;
import com.montran.internship.service.AccountService;
import com.montran.internship.service.bankcalls.BRDBankCall;
import com.montran.internship.service.bankcalls.BTBankCall;
import com.montran.internship.service.bankcalls.CecBankCall;
import com.montran.internship.service.oauth.AuthorizationService;
import com.montran.internship.util.BTUtil;
import com.montran.internship.util.CECUtil;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccountServiceImpl implements AccountService {
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private TokenRepository tokensRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private BTBankCall btBankCall;
    @Autowired
    private BRDBankCall brdBankCall;
    @Autowired
    private CecBankCall cecBankCall;

    private void saveBalance(Balance balance, Account account) {
        Balance b = balanceRepository.findByAccount(account).orElse(balance);

        if (balance != null) {
            b.setAmount(balance.getAmount());
            balanceRepository.save(b);
        }
    }

    private AccountsDTO getBTAccounts(Consent consent, String accessToken) {
        List<Account> allAccounts = accountRepository.findAllByBankAndProfile(consent.getBank(), consent.getProfile());
        TokenDTO newTokenDTO = null;

        List<AccountDTO> list = new ArrayList<>();
        String usableAccessToken = accessToken;

        for (Account account : allAccounts) {
            Balance balance = btBankCall.getAccountBalance(usableAccessToken, consent.getConsentId(), account);

            if (balance == null || btBankCall.getHasExpired()) {
                Token token = tokensRepository.findByBankAndProfile(consent.getBank(), consent.getProfile()).orElse(null);
                btBankCall.setHasExpired(false);
                if (token != null)
                    newTokenDTO = authorizationService.oauth(consent, BTUtil.authRedirect(), token.getRefreshToken(), true);
                if (newTokenDTO != null)
                    usableAccessToken = newTokenDTO.getAccessToken();

                if (usableAccessToken == null) {
                    return null;
                }

                balance = btBankCall.getAccountBalance(usableAccessToken, consent.getConsentId(), account);
            }
            if (balance == null) {
                continue;
            }
            this.saveBalance(balance, account);

            list.add(new AccountDTO(account.getIban(), account.getResourceId(), balance.getCurrency(),
                    balance.getAmount(), "Banca Transilvania"));
        };

        return new AccountsDTO(list, newTokenDTO);
    }

    private void saveBTAccounts(Consent consent, String accessToken, String refreshToken) {
        Token token = authorizationService.getToken(refreshToken);
        List<Account> accounts = btBankCall.fetchAccounts(consent, accessToken);

        if (accounts != null) {
            accounts.forEach((account) -> {
                account.setBank(consent.getBank());
                account.setProfile(consent.getProfile());
                this.saveAccount(consent, account, token);
            });
        }
    }

    private AccountsDTO getBRDAccounts(Consent consent) {

        List<Account> allAccounts = accountRepository.findAllByBankAndProfile(consent.getBank(), consent.getProfile());

        List<AccountDTO> list = new ArrayList<>();

        for (Account account : allAccounts) {
            Balance balance = brdBankCall.getAccountBalance(consent, account);

            if (balance == null) {
                continue;
            }
            this.saveBalance(balance, account);

            list.add(new AccountDTO(account.getIban(), account.getResourceId(), balance.getCurrency(),
                    balance.getAmount(), "BRD"));
        };

        return new AccountsDTO(list, null);
    }

    private void saveBRDAccounts(Consent consent) {
        List<Account> accounts = brdBankCall.fetchAccounts(consent);

        if (accounts != null) {
            accounts.forEach((account) -> {
                this.saveAccount(consent, account, null);
            });
        }
    }

    private AccountsDTO getCECAccounts(Consent consent, String accessToken) {
        List<Account> allAccounts = accountRepository.findAllByBankAndProfile(consent.getBank(), consent.getProfile());
        TokenDTO newTokenDTO = null;

        List<AccountDTO> list = new ArrayList<>();
        String usableAccessToken = accessToken;

        for (Account account : allAccounts) {
            Balance balance = cecBankCall.getAccountBalance(usableAccessToken, consent, account);

            if (balance == null || cecBankCall.getHasExpired()) {
                Token token = tokensRepository.findByBankAndProfile(consent.getBank(), consent.getProfile()).orElse(null);
                cecBankCall.setHasExpired(false);
                newTokenDTO = authorizationService.oauth(consent, CECUtil.authRedirect(), token.getRefreshToken(), true);
                usableAccessToken = newTokenDTO.getAccessToken();

                if (usableAccessToken == null) {
                    return null;
                }

                balance = cecBankCall.getAccountBalance(usableAccessToken, consent, account);
            }

            if (balance == null) {
                continue;
            }
            this.saveBalance(balance, account);

            list.add(new AccountDTO(account.getIban(), account.getResourceId(), balance.getCurrency(),
                    balance.getAmount(), "CEC Bank"));
        };

        return new AccountsDTO(list, newTokenDTO);
    }

    private void saveCECAccounts(Consent consent, String accessToken, String refreshToken) {
        Token token = authorizationService.getToken(refreshToken);
        List<Account> accounts = cecBankCall.fetchAccounts(consent, accessToken);

        if (accounts != null) {
            for (Account account : accounts) {
                account.setBank(consent.getBank());
                account.setProfile(consent.getProfile());
                this.saveAccount(consent, account, token);
            }
        }
    }

    @Override
    public AccountsDTO getAccounts(Consent consent, String accessToken) {
        if (consent == null) {
            return new AccountsDTO(new ArrayList<>());
        }

        if (accessToken.equals("null")) accessToken = null;

        if (consent.getBank().getBankName().equals(Banks.BT.label)) {
            return this.getBTAccounts(consent, accessToken);
        }

        if (consent.getBank().getBankName().equals(Banks.CEC.label)) {
            return this.getCECAccounts(consent, accessToken);
        }

        if (consent.getBank().getBankName().equals(Banks.BRD.label)) {
            return this.getBRDAccounts(consent);
        }

        return null;
    }

    @Override
    public AccountsDTO getAccountsForPayments(Profile profile) {
        List<Account> accounts = accountRepository.findAllByProfile(profile);
        List<AccountDTO> accountDTOS = accounts.stream()
                .map((account) -> new AccountDTO(
                        account.getIban(),
                        account.getResourceId(),
                        account.getBalance().getCurrency(),
                        account.getBalance().getAmount(),
                        account.getBank().getBankName()
                    ))
                .collect(Collectors.toList());

        return new AccountsDTO(accountDTOS);
    }

    @Transactional
    @Override
    public MessageResponse deleteAccount(Profile profile, String accountId) {
        Account account = accountRepository.findByResourceIdAndProfile(accountId, profile).orElse(null);

        if (account == null) {
            return new MessageResponse("The account does not exist", "400");
        }

        account.setProfile(null);
        profile.getAccounts().remove(account);
        profileRepository.save(profile);
        accountRepository.delete(account);

        return new MessageResponse("The account has been deleted successfully", "200");
    }

    @Override
    public void saveAccount(Consent consent, Account account, Token token) {
        Account acc = accountRepository.findByIbanAndProfile(account.getIban(), consent.getProfile()).orElse(null);

        if (acc != null) {
            Balance balance = acc.getBalance();

            balance.setAmount(account.getBalance().getAmount());
            acc.setBalance(balance);
        } else {
            acc = account;
        }
        System.out.println(acc.getIban());
        acc.setToken(token);
        acc.setProfile(consent.getProfile());
        acc.setBank(consent.getBank());

        accountRepository.save(acc);
    }

    @Override
    public void saveAccounts(Consent consent, String accessToken, String refreshToken) {
        if (consent.getBank().getBankName().equals(Banks.BT.label)) {
            this.saveBTAccounts(consent, accessToken, refreshToken);
        }

        if (consent.getBank().getBankName().equals(Banks.BRD.label)) {
            this.saveBRDAccounts(consent);
        }

        if (consent.getBank().getBankName().equals(Banks.CEC.label)) {
            this.saveCECAccounts(consent, accessToken, refreshToken);
        }
    }
}
