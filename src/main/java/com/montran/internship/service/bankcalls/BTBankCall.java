package com.montran.internship.service.bankcalls;

import com.montran.internship.converter.JSONConverter;
import com.montran.internship.model.*;
import com.montran.internship.payload.dto.PaymentDTO;
import com.montran.internship.payload.dto.TokenDTO;
import com.montran.internship.payload.request.PaymentRequest;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BTBankCall {
    private Boolean hasExpired;
    private final UUID uuid;

    private String generateRonPaymentBody(String debtorIban, PaymentRequest paymentRequest) {
        return "{ \"debtorAccount\": {\n" +
                "\"iban\": \"" + debtorIban + "\"\n" +
                "},\n" +
                "\"instructedAmount\": {\n" +
                "\"currency\": \"RON\",\n" +
                "\"amount\": \"" + paymentRequest.getPaymentAmount() + "\" },\n" +
                "\"creditorAccount\": {\n" +
                "\"iban\": \"" + paymentRequest.getCreditorIban() + "\" },\n" +
                "\"creditorName\": \"" + paymentRequest.getCreditorName() + "\",\n" +
                "\"debtorId\": \"" + paymentRequest.getDebtorAccountId() + "\",\n" +
                "\"endToEndIdentification\": \"TPP Reference\",\n" +
                "\"remittanceInformationUnstructured\": \"Merchant reference\" }";
    }

    private String generateOtherCurrencyPaymentBody(String debtorIban, String currency, PaymentRequest paymentRequest) {
        return "{ \"debtorAccount\": {\n" +
                "\"iban\": \"" + debtorIban + "\"\n" +
                "},\n" +
                "\"instructedAmount\": { \"currency\": \"" + currency + "\", \"amount\": \"" + paymentRequest.getPaymentAmount() + "\" }, \"creditorAccount\": {\n" +
                "\"iban\": \"" + paymentRequest.getCreditorIban() + "\" }, \"creditorName\": \"" + paymentRequest.getCreditorName() + "\", \"endToEndIdentification\": \"TPP Test Reference\", " +
                "\"remittanceInformationUnstructured\": \"detalii plata\", \"creditorAgent\": \"BTRLRO22\",\n" +
                "\"creditorAgentName\": \"BT\", \"creditorAddress\": {\n" +
                "\"country\":\"Romania\"\n" +
                "}}";
    }

    public BTBankCall() {
        hasExpired = false;
        uuid = UUID.randomUUID();
    }

    public Boolean getHasExpired() {
        return hasExpired;
    }

    public void setHasExpired(Boolean value) {
        this.hasExpired = value;
    }

    public String consent() {
        String url = "https://api.apistorebt.ro/bt/sb/bt-psd2-aisp/v2/consents";

        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                MediaType.parse("application/json"), "{\"access\": {\"availableAccounts\": \"allAccounts\"},\"recurringIndicator\": true," +
                        "\"validUntil\": \"2022-11-22\",\"combinedServiceIndicator\": false,\"frequencyPerDay\": 4}"
        );

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("x-request-id", uuid.toString())
                .addHeader("psu-ip-address", "192.168.0.100")
                .addHeader("content-type", "application/json")
                .addHeader("accept", "application/json")
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

    public Map<String, Object> register() {
        String url = "https://api.apistorebt.ro/bt/sb/oauth/register";

        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                MediaType.parse("application/json"), "{\"redirect_uris\": [\"https://localhost:3000/redirect-bt\", \"https://localhost:3000/success-payment\"], " +
                        "\"client_name\":\"Third Party Provider Application DEMO 1\"\n" +
                        "}"
        );

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
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

            return bodyResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String fetchPaymentStatus(Account account, String paymentId, String accessToken) {
        String url = "";
        if (account.getBalance().getCurrency().equals("RON")) {
            url = "https://api.apistorebt.ro/bt/sb/bt-psd2/v2/payments/ron-payment";
        } else {
            url = "https://api.apistorebt.ro/bt/sb/bt-psd2/v2/payments/other-currency-payment";
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .addHeader("x-request-id", uuid.toString())
                .addHeader("psu-ip-address", "127.0.0.1")
                .addHeader("content-type", "application/json")
                .addHeader("accept", "application/json")
                .addHeader("authorization", "Bearer " + accessToken)
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

        String url = "", bodyJson = "";
        if (account.getBalance().getCurrency().equals("RON")) {
            url = "https://api.apistorebt.ro/bt/sb/bt-psd2/v2/payments/ron-payment";
            bodyJson = this.generateRonPaymentBody(account.getResourceId(), paymentRequest);
        } else {
            url = "https://api.apistorebt.ro/bt/sb/bt-psd2/v2/payments/other-currency-payment";
            bodyJson = this.generateOtherCurrencyPaymentBody(account.getResourceId(), account.getBalance().getCurrency(),paymentRequest);
        }

        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                MediaType.parse("application/json"), bodyJson
        );

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .addHeader("x-request-id", uuid.toString())
                .addHeader("psu-ip-address", "127.0.0.1")
                .addHeader("content-type", "application/json")
                .addHeader("accept", "application/json")
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
                "client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=" + scope +":" + consentId +
                "&state=state123test" +
                "&nonce=nonce123test" +
                "&code_challenge=4vw1eoEvbd9vdGcrINTeSKydqczt_3cxCuyaQZcNnkk" +
                "&code_challenge_method=S256";
    }

    public TokenDTO getAccessToken(Consent consent, String URI, String codeToken, Boolean expired) {

        String url = "https://api.apistorebt.ro/bt/sb/oauth/token";
        String codeField = "code", grant_type = "authorization_code";

        if (expired.equals(true)) {
            codeField = "refresh_token";
            grant_type = "refresh_token";
        }

        okhttp3.RequestBody body = new FormBody.Builder()
                .add(codeField, codeToken)
                .add("grant_type", grant_type)
                .add("redirect_uri", URI)
                .add("client_id", consent.getClientId())
                .add("client_secret", consent.getClientSecret())
                .add("code_verifier", "D_zlTiLARF0xO8wmtN4200gVuhsi6Ob3OAfZUQ2U69Z9j9EPZjbHjNbTKS-" +
                        "2dVgLxxzzAYVU3YHkG93m8zPtqCfnZRMMwLzKsyJPQ0NBXtaecuozXJeUQkOIzOkh4Y_X")
                .build();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
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

    public Balance getAccountBalance(String accessToken, String consentId, Account account) {
        if (accessToken == null || account == null || accessToken.equals("null"))
            return null;

        String url = "https://api.apistorebt.ro/bt/sb/bt-psd2-aisp/v2/accounts/" + account.getResourceId() + "/balances";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("consent-id", consentId)
                .addHeader("x-request-id", uuid.toString())
                .addHeader("authorization", "Bearer " + accessToken)
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
            Map<String, Object> balances = ((List<Map<String, Object>>) bodyResponse.get("balances")).get(1);

            Balance balance = Balance.builder()
                    .currency((String)((Map<String, Object>)balances.get("balanceAmount")).get("currency"))
                    .account(account)
                    .amount((Double)((Map<String, Object>)balances.get("balanceAmount")).get("amount"))
                    .build();

            return balance;
        } catch (IOException e) {
            e.printStackTrace();
        }

            return null;
    }

    public List<Account> fetchAccounts(Consent consent, String accessToken) {
        String url = "https://api.apistorebt.ro/bt/sb/bt-psd2-aisp/v2/accounts";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("consent-id", consent.getConsentId())
                .addHeader("x-request-id", uuid.toString())
                .addHeader("authorization", "Bearer " + accessToken)
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
                        .fullName((String) account.get("name"))
                        .build();

                Balance balance = this.getAccountBalance(accessToken, consent.getConsentId(), acc);
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

    public List<Transaction> fetchTransactions(String consentId, String accessToken, Account account
                                , String dateFrom, String dateTo) {
        if (accessToken == null || account == null)
            return null;

        String url = "https://api.apistorebt.ro/bt/sb/bt-psd2-aisp/v2/accounts/" + account.getResourceId() +
                "/transactions?dateFrom=" + dateFrom + "&dateTo=" + dateTo + "&bookingStatus=booked" ;
        UUID uuid = UUID.randomUUID();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("consent-id", consentId)
                .addHeader("x-request-id", uuid.toString())
                .addHeader("authorization", "Bearer " + accessToken)
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
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse((String) tran.get("bookingDate"));

                Transaction transaction = Transaction.builder()
                                .transactionId(transactionId)
                                .creditorName((String) tran.get("creditorName"))
                                .debtorName((String) tran.get("debtorName"))
                                .currency((String) ((Map<String, Object>) tran.get("transactionAmount")).get("currency"))
                                .amount((String) ((Map<String, Object>) tran.get("transactionAmount")).get("amount"))
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
