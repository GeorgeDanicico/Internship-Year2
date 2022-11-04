package com.montran.internship.service.oauth;


import com.montran.internship.model.*;
import com.montran.internship.model.enums.Banks;
import com.montran.internship.payload.dto.TokenDTO;
import com.montran.internship.repository.*;
import com.montran.internship.service.bankcalls.BTBankCall;
import com.montran.internship.service.bankcalls.CecBankCall;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class AuthorizationService {
    @Autowired
    private ConsentRepository consentRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private BTBankCall btBankCall;
    @Autowired
    private CecBankCall cecBankCall;

    private Consent generateNewCredentials(Consent consent) {
        Map<String, Object> registerResponse = btBankCall.register();
        String clientId = (String) registerResponse.get("client_id");
        String clientSecret = (String) registerResponse.get("client_secret");
        consent.setClientId(clientId);
        consent.setClientSecret(clientSecret);
        consentRepository.save(consent);

        return consent;
    }

    private void saveToken(TokenDTO tokenDTO, Consent consent) {
        if (tokenDTO != null && tokenDTO.getRefreshToken() != null) {
            Token token = tokenRepository.findByBankAndProfile(consent.getBank(), consent.getProfile()).orElse(
                    Token.builder()
                            .bank(consent.getBank())
                            .profile(consent.getProfile())
                            .build()
            );
            token.setRefreshToken(tokenDTO.getRefreshToken());
            tokenRepository.save(token);
        }
    }

    public TokenDTO oauth(Consent consent, String URI, String code, Boolean expired) {
        if (consent.getBank().getBankName().equals(Banks.BT.label)) {
            TokenDTO tokenDTO = btBankCall.getAccessToken(consent, URI, code, expired);
            this.saveToken(tokenDTO, consent);

            return tokenDTO;
        }

        if (consent.getBank().getBankName().equals(Banks.CEC.label)) {
            TokenDTO tokenDTO = cecBankCall.getAccessToken(consent, URI, code, expired);
            this.saveToken(tokenDTO, consent);

            return tokenDTO;
        }

        return null;
    }

    public TokenDTO paymentAuth(Consent consent, String URI, String code, Boolean expired) {
        if (consent.getBank().getBankName().equals(Banks.BT.label)) {
            TokenDTO tokenDTO = btBankCall.getAccessToken(consent, URI, code, expired);

            return tokenDTO;
        }

        if (consent.getBank().getBankName().equals(Banks.CEC.label)) {
            TokenDTO tokenDTO = cecBankCall.getAccessToken(consent, URI, code, expired);

            return tokenDTO;
        }

        return null;
    }

    public Token getToken(String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken).orElse(null);
    }
}
