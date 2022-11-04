package com.montran.internship.controller;

import com.montran.internship.model.Consent;
import com.montran.internship.model.Profile;
import com.montran.internship.payload.dto.TransactionsDTO;
import com.montran.internship.service.ConsentService;
import com.montran.internship.service.ProfileService;
import com.montran.internship.service.TransactionService;
import com.montran.internship.service.bankcalls.BRDBankCall;
import com.montran.internship.service.bankcalls.BTBankCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private ConsentService consentService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ProfileService profileService;

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccountTransactions(@Valid
                                                        @RequestParam String bankName,
                                                        @RequestParam String accessToken,
                                                    @RequestParam String dateFrom,
                                                    @RequestParam String dateTo,
                                                    @PathVariable String accountId) {
        Profile profile = profileService.getLoggedProfile();
        Consent consent = consentService.getConsent(profile, bankName);

        TransactionsDTO transactionsDTO = transactionService.getAccountTransactions(
                consent, accessToken, accountId, dateFrom, dateTo);
//
        if (transactionsDTO == null) {
            return new ResponseEntity("An error has occurred", HttpStatus.BAD_REQUEST);
        }
//
        return new ResponseEntity(transactionsDTO, HttpStatus.ACCEPTED);

    }
}
