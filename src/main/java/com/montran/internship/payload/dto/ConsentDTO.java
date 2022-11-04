package com.montran.internship.payload.dto;

public class ConsentDTO {
    private String consentId;
    private String redirectLink;

    public ConsentDTO(String consentId, String redirectLink) {
        this.consentId = consentId;
        this.redirectLink = redirectLink;
    }

    public String getConsentId() {
        return consentId;
    }

    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }

    public String getRedirectLink() {
        return redirectLink;
    }

    public void setRedirectLink(String redirectLink) {
        this.redirectLink = redirectLink;
    }
}
