package com.montran.internship.service;

import com.montran.internship.model.Bank;
import com.montran.internship.model.Consent;
import com.montran.internship.model.Profile;
import com.montran.internship.payload.dto.ConsentDTO;

public interface ConsentService {
    void saveConsent(Profile profile, Bank bank, String newConsentId, String newClientId, String newClientSecret);
    ConsentDTO getConsentAndRedirectLink(Profile profile, String bankName);
    Consent getConsent(Profile profile, String bankName);
}
