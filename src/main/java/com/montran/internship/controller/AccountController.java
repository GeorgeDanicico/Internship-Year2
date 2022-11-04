package com.montran.internship.controller;

import com.montran.internship.model.Consent;
import com.montran.internship.model.Profile;
import com.montran.internship.model.Token;
import com.montran.internship.payload.dto.AccountDTO;
import com.montran.internship.payload.dto.AccountsDTO;
import com.montran.internship.payload.response.MessageResponse;
import com.montran.internship.service.AccountService;
import com.montran.internship.service.ConsentService;
import com.montran.internship.service.ProfileService;
import com.montran.internship.service.bankcalls.BRDBankCall;
import com.montran.internship.service.bankcalls.BTBankCall;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private ProfileService profileService;
    @Autowired
    private ConsentService consentService;
    @Autowired
    private AccountService accountService;

    @GetMapping("/")
    public ResponseEntity<?> getBankAccounts(@Valid @RequestParam String accessToken,
                                             @RequestParam String bankName) {
        Profile profile = profileService.getLoggedProfile();
        Consent consent = consentService.getConsent(profile, bankName);
        AccountsDTO accountsDTO = accountService.getAccounts(consent, accessToken);

        if (accountsDTO == null) {
            return new ResponseEntity("An error has occurred", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(accountsDTO, HttpStatus.ACCEPTED);
    }

    @GetMapping("/for-payments")
    public ResponseEntity<?> getBankAccountsForPayments() {
        Profile profile = profileService.getLoggedProfile();
        AccountsDTO accountsDTO = accountService.getAccountsForPayments(profile);

        if (accountsDTO == null) {
            return new ResponseEntity("An error has occurred", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(accountsDTO, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete/{accountId}")
    public ResponseEntity<?> deleteBankAccount(@Valid @PathVariable String accountId) {
        Profile profile = profileService.getLoggedProfile();

        MessageResponse messageResponse = this.accountService.deleteAccount(profile, accountId);
        return new ResponseEntity(messageResponse,
                HttpStatus.ACCEPTED);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveBankAccounts(@Valid @RequestParam String accessToken,
                                              @RequestParam String refreshToken,
                                              @RequestParam String bankName) {
        Profile profile = profileService.getLoggedProfile();
        Consent consent = consentService.getConsent(profile, bankName);

        this.accountService.saveAccounts(consent, accessToken, refreshToken);
        return new ResponseEntity(new MessageResponse("Accounts have been saved"),
                HttpStatus.ACCEPTED);
    }
}
