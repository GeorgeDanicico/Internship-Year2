package com.montran.internship.controller;

import com.montran.internship.model.Consent;
import com.montran.internship.model.Profile;
import com.montran.internship.payload.dto.TokenDTO;
import com.montran.internship.payload.dto.ConsentDTO;
import com.montran.internship.service.AccountService;
import com.montran.internship.service.ConsentService;
import com.montran.internship.service.ProfileService;
import com.montran.internship.service.bankcalls.BRDBankCall;
import com.montran.internship.service.bankcalls.BTBankCall;
import com.montran.internship.service.oauth.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bank")
public class BankController {
    @Autowired
    private ConsentService consentService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private AccountService accountService;

    @PostMapping("/")
    public ResponseEntity<?> getConsent(@Valid @RequestParam String bankName) {
        Profile profile = profileService.getLoggedProfile();
        ConsentDTO consentDTO = consentService.getConsentAndRedirectLink(profile, bankName);

        return new ResponseEntity(consentDTO, HttpStatus.ACCEPTED);
    }

    @PostMapping("/oauth/token")
    public ResponseEntity<?> getOauthToken(@Valid @RequestParam String code,
                                           @RequestParam String bankName,
                                           @RequestParam String redirectURI) {
        Profile profile = profileService.getLoggedProfile();
        Consent consent = consentService.getConsent(profile, bankName);

        TokenDTO tokenDto = authorizationService.oauth(consent, redirectURI, code, false);

        if (tokenDto == null) {
            return new ResponseEntity("An error has occurred", HttpStatus.BAD_REQUEST);
        }

        accountService.saveAccounts(consent, tokenDto.getAccessToken(), tokenDto.getRefreshToken());

        return new ResponseEntity(tokenDto, HttpStatus.ACCEPTED);
    }
}
