package com.montran.internship.service.impl;

import com.montran.internship.model.Bank;
import com.montran.internship.model.Consent;
import com.montran.internship.model.Profile;
import com.montran.internship.model.enums.Banks;
import com.montran.internship.payload.dto.ConsentDTO;
import com.montran.internship.repository.BankRepository;
import com.montran.internship.repository.ConsentRepository;
import com.montran.internship.repository.ProfileRepository;
import com.montran.internship.service.ConsentService;
import com.montran.internship.service.bankcalls.BRDBankCall;
import com.montran.internship.service.bankcalls.BTBankCall;
import com.montran.internship.service.bankcalls.CecBankCall;
import com.montran.internship.util.CECUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ConsentServiceImpl implements ConsentService {
    @Autowired
    private ConsentRepository consentRepository;
    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private BTBankCall btBankCall;
    @Autowired
    private BRDBankCall brdBankCall;
    @Autowired
    private CecBankCall cecBankCall;

    private ConsentDTO getBTConsent(Profile profile, Bank bank) {
        Consent consent = consentRepository.findByBankAndProfile(bank, profile).orElse(null);

        String consentId = "";
        if (consent == null) {
            consentId = btBankCall.consent();
        } else {
            consentId = consent.getConsentId();
        }

        Map<String, Object> registerResponse = btBankCall.register();
        String clientId = (String) registerResponse.get("client_id");
        String clientSecret = (String) registerResponse.get("client_secret");

        this.saveConsent(profile, bank, consentId, clientId, clientSecret);

        final String url = "https://apistorebt.ro/auth/realms/psd2-sb/protocol/openid-connect/auth?";
        final String redirectUri = "https://localhost:3000/redirect-bt";

        return new ConsentDTO(consentId, btBankCall.generateRedirectLink(url, redirectUri, clientId, consentId, "AIS"));
    }

    private ConsentDTO getBRDConsent(Profile profile, Bank bank) {
        Consent consent = consentRepository.findByBankAndProfile(bank, profile).orElse(null);

        String consentId = "";
        if (consent == null) {
            consentId = brdBankCall.consent();;
        } else {
            consentId = consent.getConsentId();
        }

        this.saveConsent(profile, bank, consentId, null, null);

        return new ConsentDTO(consentId, "https://localhost:3000/redirect-brd");
    }

    private ConsentDTO getCecConsent(Profile profile, Bank bank) {
        Consent consent = consentRepository.findByBankAndProfile(bank, profile).orElse(null);

        String consentId = "";
        if (consent == null) {
            consentId = cecBankCall.consent();
        } else {
            consentId = consent.getConsentId();
        }

        this.saveConsent(profile, bank, consentId, CECUtil.getClientId(), CECUtil.getClientSecret());

        return new ConsentDTO(consentId, cecBankCall.generateRedirectLink(CECUtil.oauthLink(),
                CECUtil.authRedirect(), CECUtil.getClientId(), consentId, "AIS"));
    }

    @Override
    public void saveConsent(Profile profile, Bank bank, String newConsentId, String newClientId, String newClientSecret) {
        Consent existingConsent = consentRepository.findByConsentIdAndProfile(newConsentId, profile).orElse(null);

        if (existingConsent == null) {

             existingConsent = Consent.builder()
                    .consentId(newConsentId)
                    .clientId(newClientId)
                    .clientSecret(newClientSecret)
                    .profile(profile)
                    .bank(bank)
                    .build();

            bank.getConsents().add(existingConsent);
        } else {
            existingConsent.setClientId(newClientId);
            existingConsent.setClientSecret(newClientSecret);
        }

        consentRepository.save(existingConsent);
    }

    @Override
    public ConsentDTO getConsentAndRedirectLink(Profile profile, String bankName) {

        Bank bank = bankRepository.findByBankName(bankName).orElseThrow();

        if (bankName.equals(Banks.BT.label)) {
            return this.getBTConsent(profile, bank);
        }

        if (bankName.equals(Banks.BRD.label)) {
            return this.getBRDConsent(profile, bank);
        }

        if (bankName.equals(Banks.CEC.label)) {
            return this.getCecConsent(profile, bank);
        }

        return null;
    }

    @Override
    public Consent getConsent(Profile profile, String bankName) {
        Bank bank = bankRepository.findByBankName(bankName).orElse(null);

        return this.consentRepository.findByBankAndProfile(bank, profile).orElse(null);
    }
}
