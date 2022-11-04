package com.montran.internship.service.bankcalls;

import com.montran.internship.converter.JSONConverter;
import com.montran.internship.model.Account;
import com.montran.internship.model.Balance;
import com.montran.internship.model.Consent;
import com.montran.internship.model.Transaction;
import com.montran.internship.payload.dto.TokenDTO;
import com.montran.internship.payload.request.PaymentRequest;
import com.montran.internship.util.CECUtil;
import okhttp3.*;
import okhttp3.tls.Certificates;
import okhttp3.tls.HandshakeCertificates;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CecBankCall {
    private final UUID uuid;
    private Boolean hasExpired;

    public CecBankCall() {
        this.uuid = UUID.randomUUID();
        this.hasExpired = false;
    }

    final String cecCertificate = "-----BEGIN CERTIFICATE-----MIIENjCCAx6gAwIBAgIEXkKZvjANBgkqhkiG9w0BAQsFADByMR8wHQYDVQQDDBZBcHBDZXJ0aWZp" +
            "Y2F0ZU1lYW5zQVBJMQwwCgYDVQQLDANJTkcxDDAKBgNVBAoMA0lORzESMBAGA1UEBwwJQW1zdGVyZGFtMRIwEAYDVQQIDAlBbXN0ZXJkY" +
            "W0xCzAJBgNVBAYTAk5MMB4XDTIwMDIxMDEyMTAzOFoXDTIzMDIxMTEyMTAzOFowPjEdMBsGA1UECwwUc2FuZGJveF9laWRhc19xc2VhbG" +
            "MxHTAbBgNVBGEMFFBTRE5MLVNCWC0xMjM0NTEyMzQ1MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkJltvbEo4/SFcvtGiRC" +
            "ar7Ah/aP0pY0bsAaCFwdgPikzFj+ij3TYgZLykz40EHODtG5Fz0iZD3fjBRRM/gsFPlUPSntgUEPiBG2VUMKbR6P/KQOzmNKF7zcOly0J" +
            "VOyWcTTAi0VAl3MEO/nlSfrKVSROzdT4Aw/h2RVy5qlw66jmCTcp5H5kMiz6BGpG+K0dxqBTJP1WTYJhcEj6g0r0SYMnjKxBnztuhX5Xy" +
            "lqoVdUy1a1ouMXU8IjWPDjEaM1TcPXczJFhakkAneoAyN6ztrII2xQ5mqmEQXV4BY/iQLT2grLYOvF2hlMg0kdtK3LXoPlbaAUmXCoO8V" +
            "CfyWZvqwIDAQABo4IBBjCCAQIwNwYDVR0fBDAwLjAsoCqgKIYmaHR0cHM6Ly93d3cuaW5nLm5sL3Rlc3QvZWlkYXMvdGVzdC5jcmwwHwY" +
            "DVR0jBBgwFoAUcEi7XgDA9Cb4xHTReNLETt+0clkwHQYDVR0OBBYEFLQI1Hig4yPUm6xIygThkbr60X8wMIGGBggrBgEFBQcBAwR6MHgw" +
            "CgYGBACORgEBDAAwEwYGBACORgEGMAkGBwQAjkYBBgIwVQYGBACBmCcCMEswOTARBgcEAIGYJwEDDAZQU1BfQUkwEQYHBACBmCcBAQwGU" +
            "FNQX0FTMBEGBwQAgZgnAQIMBlBTUF9QSQwGWC1XSU5HDAZOTC1YV0cwDQYJKoZIhvcNAQELBQADggEBAEW0Rq1KsLZooH27QfYQYy2MRp" +
            "ttoubtWFIyUV0Fc+RdIjtRyuS6Zx9j8kbEyEhXDi1CEVVeEfwDtwcw5Y3w6Prm9HULLh4yzgIKMcAsDB0ooNrmDwdsYcU/Oju23ym+6rW" +
            "RcPkZE1on6QSkq8avBfrcxSBKrEbmodnJqUWeUv+oAKKG3W47U5hpcLSYKXVfBK1J2fnk1jxdE3mWeezoaTkGMQpBBARN0zMQGOTNPHKS" +
            "sTYbLRCCGxcbf5oy8nHTfJpW4WO6rK8qcFTDOWzsW0sRxYviZFAJd8rRUCnxkZKQHIxeJXNQrrNrJrekLH3FbAm/LkyWk4Mw1w0TnQLAq" +
            "+s=-----END CERTIFICATE-----";

    public Boolean getHasExpired() {
        return hasExpired;
    }

    public void setHasExpired(Boolean hasExpired) {
        this.hasExpired = hasExpired;
    }

    public String consent() {
        String url = "https://api-test.cec.ro/cec/tpp/psd29c/v1/consents";

        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                MediaType.parse("application/json"), ""
        );

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("x-request-id", uuid.toString())
                .addHeader("psu-ip-address", "192.168.0.100")
                .addHeader("content-type", "application/json")
                .addHeader("accept", "application/json")
                .addHeader("tpp-redirect-uri", "https://localhost:3000/redirect-cec")
                .addHeader("tpp-signature-certificate", cecCertificate)
                .addHeader("x-ibm-client-id", CECUtil.getClientId())
                .build();

        Call call = client.newCall(request);
        try {
            Response response = call.execute();

            if (response.code() > 299) {
                response.close();
                return null;
            }
            JSONObject obj = new JSONObject(response.body().string());
            Map<String, Object> bodyResponse = JSONConverter.toMap(obj);

            String consentId = (String) bodyResponse.get("consentId");
            if (consentId != null) {
                response.close();
            }

            response.close();
            return consentId;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String fetchPaymentStatus(Consent consent, Account account, String paymentId, String accessToken) {
        String url = "https://api-test.cec.ro/cec/tpp/psd29p/v1/payments/sepa-credit-transfer";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .addHeader("x-request-id", uuid.toString())
                .addHeader("psu-ip-address", "127.0.0.1")
                .addHeader("content-type", "application/json")
                .addHeader("accept", "application/json")
                .addHeader("authorization", "Bearer " + accessToken)
                .addHeader("x-ibm-client-id", consent.getClientId())
                .addHeader("tpp-signature-certificate", cecCertificate)
                .url(url + "/" + paymentId + "/status")
                .build();

        Call call = client.newCall(request);
        try {
            Response response = call.execute();

            if (response.code() > 299) {
                response.close();
                return null;
            }

            JSONObject obj = new JSONObject(response.body().string());
            Map<String, Object> bodyResponse = JSONConverter.toMap(obj);

            return (String) bodyResponse.get("transactionStatus");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Map<String, String> payment(Account account, PaymentRequest paymentRequest) {

        String url = "https://api-test.cec.ro/cec/tpp/psd29p/v1/payments/sepa-credit-transfer";
        String bodyJson = "{ \"debtorAccount\": {\n" +
                "\"iban\": \"" + account.getIban() + "\"\n" +
                "},\n" +
                "\"instructedAmount\": {\n" +
                "\"currency\": \"" + account.getBalance().getCurrency() + "\",\n" +
                "\"amount\": \"" + paymentRequest.getPaymentAmount() + "\" },\n" +
                "\"creditorAccount\": {\n" +
                "\"iban\": \"" + paymentRequest.getCreditorIban() + "\" },\n" +
                "\"creditorName\": \"" + paymentRequest.getCreditorName() + "\",\n" +
                "\"debtorId\": \"" + paymentRequest.getDebtorAccountId() + "\",\n" +
                "\"endToEndIdentification\": \"TPP Reference\",\n" +
                "\"remittanceInformationUnstructured\": \"" + paymentRequest.getDetails() + "\" }";

        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                MediaType.parse("application/json"), bodyJson
        );

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .addHeader("x-request-id", uuid.toString())
                .addHeader("psu-ip-address", "127.0.0.1")
                .addHeader("content-type", "application/json")
                .addHeader("accept", "application/json")
                .addHeader("tpp-redirect-uri", CECUtil.paymentRedirect())
                .addHeader("tpp-signature-certificate", cecCertificate)
                .addHeader("x-ibm-client-id", CECUtil.getClientId())
                .url(url)
                .post(body)
                .build();

        Call call = client.newCall(request);
        try {
            Response response = call.execute();

            if (response.code() > 299) {
                response.close();
                return null;
            }

            JSONObject obj = new JSONObject(response.body().string());
            Map<String, Object> bodyResponse = JSONConverter.toMap(obj);
            String paymentId = (String) bodyResponse.get("paymentId");
            String paymentStatus = (String) bodyResponse.get("transactionStatus");

            Map<String, String> result = new HashMap<>();
            result.put("paymentId", paymentId);
            result.put("paymentStatus", paymentStatus);

            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public String generateRedirectLink(String url, String redirectUri, String clientId, String consentId, String scope) {
        return url +
                "?response_type=code" +
                "&scope=" + scope +":" + consentId +
                "&redirect_uri=" + redirectUri +
                "&client_id=" + clientId +
                "&code_challenge=4vw1eoEvbd9vdGcrINTeSKydqczt_3cxCuyaQZcNnkk" +
                "&code_challenge_method=S256";
    }

    public TokenDTO getAccessToken(Consent consent, String URI, String codeToken, Boolean expired) {

        String url = "https://api-test.cec.ro/cec/tpp/oauthcec/oauth2/token";
        okhttp3.RequestBody body = null;

        if (expired.equals(true)) {
            body = new FormBody.Builder()
                    .add("grant_type", "refresh_token")
                    .add("client_id", consent.getClientId())
                    .add("refresh_token", codeToken).build();
        } else {
            body = new FormBody.Builder()
                    .add("grant_type", "authorization_code")
                    .add("client_id", consent.getClientId())
                    .add("code", codeToken)
                    .add("redirect_uri", URI)
                    .add("scope", "AIS:" + consent.getConsentId())
                    .add("code_verifier", "D_zlTiLARF0xO8wmtN4200gVuhsi6Ob3OAfZUQ2U69Z9j9EPZjbHjNbTKS-"
                            + "2dVgLxxzzAYVU3YHkG93m8zPtqCfnZRMMwLzKsyJPQ0NBXtaecuozXJeUQkOIzOkh4Y_X")
                    .build();
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("x-ibm-client-id", consent.getClientId())
                .build();

        Call call = client.newCall(request);

        try {
            Response response = call.execute();

            if (response.code() > 299) {
                response.close();
                return null;
            }

            JSONObject obj = new JSONObject(response.body().string());
            Map<String, Object> bodyResponse = JSONConverter.toMap(obj);
            TokenDTO tokenDTO = new TokenDTO(
                    (String) bodyResponse.get("access_token"),
                    (String) bodyResponse.get("token_type"),
                    (String) bodyResponse.get("refresh_token"),
                    (Integer) bodyResponse.get("expires_in")
            );

            return tokenDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Balance getAccountBalance(String accessToken, Consent consent, Account account) {
        if (accessToken == null || account == null || accessToken.equals("null"))
            return null;

        String url = "https://api-test.cec.ro/cec/tpp/psd29c/v1/accounts/" + account.getResourceId() + "/balances";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("consent-id", consent.getConsentId())
                .addHeader("x-request-id", uuid.toString())
                .addHeader("x-ibm-client-id", consent.getClientId())
                .addHeader("authorization", "Bearer " + accessToken)
                .addHeader("tpp-signature-certificate", cecCertificate)
                .addHeader("psu-ip-address", "127.0.0.1")
                .build();

        Call call = client.newCall(request);

        try {
            Response response = call.execute();

            if (response.code() == 401) {
                this.hasExpired = true;
            }

            if (response.code() > 299) {
                response.close();
                return null;
            }

            JSONObject obj = new JSONObject(response.body().string());
            Map<String, Object> bodyResponse = JSONConverter.toMap(obj);
            Map<String, Object> balances = ((List<Map<String, Object>>) bodyResponse.get("balances")).get(0);

            Balance balance = Balance.builder()
                    .currency((String)((Map<String, Object>)balances.get("balanceAmount")).get("currency"))
                    .account(account)
                    .amount(Double.parseDouble((String)((Map<String, Object>)balances.get("balanceAmount")).get("amount")))
                    .build();

            return balance;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Account> fetchAccounts(Consent consent, String accessToken) {
        String url = "https://api-test.cec.ro/cec/tpp/psd29c/v1/accounts";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("consent-id", consent.getConsentId())
                .addHeader("x-request-id", uuid.toString())
                .addHeader("x-ibm-client-id", consent.getClientId())
                .addHeader("authorization", "Bearer " + accessToken)
                .addHeader("tpp-signature-certificate", cecCertificate)
                .addHeader("psu-ip-address", "127.0.0.1")
                .build();

        Call call = client.newCall(request);

        try {
            Response response = call.execute();

            if (response.code() > 299) {
                response.close();
                return null;
            }

            JSONObject obj = new JSONObject(response.body().string());
            Map<String, Object> bodyResponse = JSONConverter.toMap(obj);

            List<Map<String, Object>> accountsResponse = (List<Map<String, Object>>) bodyResponse.get("accounts");
            List<Account> accounts = new ArrayList<>();

            for (var account : accountsResponse){
                Account acc = Account.builder()
                        .resourceId((String) account.get("resourceId"))
                        .iban((String) account.get("iban"))
                        .fullName((String) account.get("product"))
                        .build();

                Balance balance = this.getAccountBalance(accessToken, consent, acc);
                acc.setBalance(balance);
                accounts.add(acc);
            }

            response.close();
            return accounts;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Transaction> fetchTransactions(Consent consent, String accessToken, Account account
            , String dateFrom, String dateTo) {
        if (accessToken == null || account == null)
            return null;

        String url = "https://api-test.cec.ro/cec/tpp/psd29c/v1/accounts/" + account.getResourceId() +
                "/transactions?dateFrom=" + dateFrom + "&dateTo=" + dateTo + "&bookingStatus=booked";
        UUID uuid = UUID.randomUUID();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("consent-id", consent.getConsentId())
                .addHeader("x-ibm-client-id", consent.getClientId())
                .addHeader("x-request-id", uuid.toString())
                .addHeader("authorization", "Bearer " + accessToken)
                .addHeader("tpp-signature-certificate", cecCertificate)
                .addHeader("psu-ip-address", "127.0.0.1")
                .build();

        Call call = client.newCall(request);

        try {
            Response response = call.execute();

            if (response.code() == 401) {
                this.hasExpired = true;
            }

            if (response.code() > 299) {
                response.close();
                return null;
            }

            JSONObject obj = new JSONObject(response.body().string());
            Map<String, Object> bodyResponse = JSONConverter.toMap(obj);
            List<Map<String, Object>> transactionsResponse = (List<Map<String, Object>>) ((Map<String, Object>)
                    bodyResponse.get("transactions")).get("booked");
            List<Transaction> transactions = new ArrayList<>();

            for (var tran : transactionsResponse) {
                String transactionId = (String) tran.get("transactionId");
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse((String) tran.get("valueDate"));

                Transaction transaction = Transaction.builder()
                        .transactionId(transactionId)
                        .creditorName((String) tran.get("creditorName"))
                        .debtorName((String) tran.get("debtorName"))
                        .currency((String) ((Map<String, Object>) tran.get("transactionAmount")).get("currency"))
                        .amount(String.valueOf(((Map<String, Object>) tran.get("transactionAmount")).get("amount")))
                        .account(account)
                        .date(date)
                        .build();

                transactions.add(transaction);
            }

            return transactions;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
