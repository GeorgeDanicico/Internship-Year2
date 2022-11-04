package com.montran.internship.util;

public class CECUtil {

    public static String oauthLink() {
        return "https://api-test.cec.ro/cec/tpp/oauthcec/oauth2/authorize";
    }

    public static String authRedirect() {
        return "https://localhost:3000/redirect-cec";
    }

    public static String paymentRedirect() {
        return "https://localhost:3000/success-payment-cec";
    }

    public static String getClientId() {
        return "3b692ec8-fbcf-41aa-80f5-b535ac2a7852";
    }

    public static String getClientSecret() {
        return "bN1qD2tX8uH8kY0rH8eV2vB8rX6cB2bV4cX3sW0eB1jY7jT8bH";
    }
}
