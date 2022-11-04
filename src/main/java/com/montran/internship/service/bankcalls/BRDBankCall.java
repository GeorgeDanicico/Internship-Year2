package com.montran.internship.service.bankcalls;

import com.montran.internship.converter.JSONConverter;
import com.montran.internship.model.*;
import com.montran.internship.payload.dto.PaymentDTO;
import com.montran.internship.payload.request.PaymentRequest;
import okhttp3.*;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class BRDBankCall{
    private final UUID uuid;

    public BRDBankCall() {
        uuid = UUID.randomUUID();
    }

    private String generatePaymentBody(Profile profile, Account account, PaymentRequest paymentRequest) {
        return "<Document\n" +
                "    xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\">\n" +
                "    <CstmrCdtTrfInitn>\n" +
                "        <GrpHdr>\n" +
                "            <MsgId>3101201914590991</MsgId>\n" +
                "            <CreDtTm>" + new Date() + "</CreDtTm>\n" +
                "            <NbOfTxs>1</NbOfTxs>\n" +
                "            <CtrlSum>" + paymentRequest.getPaymentAmount() + "</CtrlSum>\n" +
                "            <InitgPty />\n" +
                "        </GrpHdr>\n" +
                "        <PmtInf>\n" +
                "            <PmtInfId>31012019PMT14590991</PmtInfId>\n" +
                "            <PmtMtd>TRF</PmtMtd>\n" +
                "            <BtchBookg>false</BtchBookg>\n" +
                "            <NbOfTxs>1</NbOfTxs>\n" +
                "            <CtrlSum>" + paymentRequest.getPaymentAmount() + "</CtrlSum>\n" +
                "            <PmtTpInf>\n" +
                "                <SvcLvl>\n" +
                "                    <Cd>SEPA</Cd>\n" +
                "                </SvcLvl>\n" +
                "            </PmtTpInf>\n" +
                "            <ReqdExctnDt>2019-01-31</ReqdExctnDt>\n" +
                "            <Dbtr>\n" +
                "                <Nm>" + profile.getFirstName() + " " + profile.getLastName() + "</Nm>\n" +
                "            </Dbtr>\n" +
                "            <DbtrAcct>\n" +
                "                <Id>\n" +
                "                    <IBAN>" + account.getIban() + "</IBAN>\n" +
                "                </Id>\n" +
                "            </DbtrAcct>\n" +
                "            <DbtrAgt>\n" +
                "                <FinInstnId>\n" +
                "                    <BIC>BRDEROBU</BIC>\n" +
                "                    <Othr>\n" +
                "                        <Id>BRDE</Id>\n" +
                "                    </Othr>\n" +
                "                </FinInstnId>\n" +
                "            </DbtrAgt>\n" +
                "            <CdtTrfTxInf>\n" +
                "                <PmtId>\n" +
                "                    <EndToEndId>201913114571575</EndToEndId>\n" +
                "                </PmtId>\n" +
                "                <PmtTpInf>\n" +
                "                    <InstrPrty>NORM</InstrPrty>\n" +
                "                </PmtTpInf>\n" +
                "                <Amt>\n" +
                "                    <InstdAmt Ccy=\"" + account.getBalance().getCurrency() + "\">" + paymentRequest.getPaymentAmount() + "</InstdAmt>\n" +
                "                </Amt>\n" +
                "                <ChrgBr>SLEV</ChrgBr>\n" +
                "                <CdtrAgt>\n" +
                "                    <FinInstnId>\n" +
                "                        <BIC>BRDEROBU</BIC>\n" +
                "                        <Nm>BRD GROUPE SOCIETE GENERALE SA</Nm>\n" +
                "                        <PstlAdr>\n" +
                "                            <StrtNm>1 7 ION MIHALACHE BOULEVARD</StrtNm>\n" +
                "                            <TwnNm>011171 BUCHAREST</TwnNm>\n" +
                "                            <Ctry>RO</Ctry>\n" +
                "                        </PstlAdr>\n" +
                "                    </FinInstnId>\n" +
                "                </CdtrAgt>\n" +
                "                <Cdtr>\n" +
                "                    <Nm>" + paymentRequest.getCreditorName() + "</Nm>\n" +
                "                    <PstlAdr>\n" +
                "                        <StrtNm>S1</StrtNm>\n" +
                "                        <TwnNm>BUC</TwnNm>\n" +
                "                        <Ctry>RO</Ctry>\n" +
                "                    </PstlAdr>\n" +
                "                </Cdtr>\n" +
                "                <CdtrAcct>\n" +
                "                    <Id>\n" +
                "                        <IBAN>" + paymentRequest.getCreditorIban() + "</IBAN>\n" +
                "                    </Id>\n" +
                "                </CdtrAcct>\n" +
                "            </CdtTrfTxInf>\n" +
                "        </PmtInf>\n" +
                "    </CstmrCdtTrfInitn>\n" +
                "</Document>";
    }

    public String consent() {
        String url = "https://api.devbrd.ro/brd-api-connect-prod-organization/apicatalog/brd-psd2-aisp/v1/consents";
        String json = "{\"access\": {\"allPsd2\": \"allAccounts\"},\"recurringIndicator\": true," +
                "\"validUntil\": \"2022-08-22\",\"combinedServiceIndicator\": false,\"frequencyPerDay\": 4}";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .setHeader("accept", "application/json")
                .setHeader("content-type", "application/json")
                .setHeader("x-request-id", uuid.toString())
                .setHeader("psu-ip-address", "127.0.0.1")
                .setHeader("psu-id", "13333330")
                .build();

        try {
            HttpResponse response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() > 299) {
                return null;
            }

            JSONObject obj = new JSONObject(response.body().toString());
            Map<String, Object> bodyResponse = JSONConverter.toMap(obj);
//
            String consentId = (String) bodyResponse.get("consentId");

            return consentId;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> payment(Profile profile, Account account, PaymentRequest paymentRequest) {
        String url = "https://api.devbrd.ro/brd-api-connect-prod-organization/apicatalog/brd-psd2-pisp/v1/" +
                "payments/pain.001-sepa-credit-transfers";

        String bodyJson = this.generatePaymentBody(profile, account, paymentRequest);

        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                bodyJson, null
        );

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .addHeader("x-request-id", uuid.toString())
                .addHeader("psu-ip-address", "127.0.0.1")
                .addHeader("content-type", "application/xml")
                .addHeader("accept", "application/json")
                .addHeader("psu-id", "13333330")
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

    public Balance getAccountBalance(Consent consent, Account account) {
        if (account == null || consent == null)
            return null;

        String url = "https://api.devbrd.ro/brd-api-connect-prod-organization/apicatalog/brd-psd2-aisp/v1/accounts/" +
                account.getResourceId() + "/balances";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-request-id", uuid.toString())
                .addHeader("content-type", "application/json")
                .addHeader("consent-id", consent.getConsentId())
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
            Map<String, Object> accountBalance = ((List<Map<String, Object>>) bodyResponse.get("balances")).get(0);

            Double amount = Double.parseDouble((String)((Map<String, Object>)accountBalance.get("balanceAmount")).get("amount"));
            Balance balance = Balance.builder()
                    .currency((String)((Map<String, Object>)accountBalance.get("balanceAmount")).get("currency"))
                    .account(account)
                    .amount(amount)
                    .build();

            response.close();
            return balance;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Account> fetchAccounts(Consent consent) {
        String url = "https://api.devbrd.ro/brd-api-connect-prod-organization/apicatalog/brd-psd2-aisp/v1/accounts";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-request-id", uuid.toString())
                .addHeader("content-type", "application/json")
                .addHeader("consent-id", consent.getConsentId())
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

            List<Map<String, Object>> accountsResponse = (List<Map<String, Object>>) bodyResponse.get("accounts");
            List<Account> accounts = new ArrayList<>();

            for (var account : accountsResponse){
                Account acc = Account.builder()
                        .resourceId((String) account.get("resourceId"))
                        .iban((String) account.get("iban"))
                        .fullName((String) account.get("name"))
                        .build();

                Balance balance = this.getAccountBalance(consent, acc);
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

    public List<Transaction> fetchTransactions(String consentId, Account account
            , String dateFrom, String dateTo) {
        if (account == null)
            return null;

        String url = "https://api.devbrd.ro/brd-api-connect-prod-organization/apicatalog/brd-psd2-aisp/v1/accounts/" + account.getResourceId() +
                "/transactions?dateFrom=" + dateFrom + "&dateTo=" + dateTo + "&bookingStatus=booked" ;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/json")
                .addHeader("consent-id", consentId)
                .addHeader("x-request-id", uuid.toString())
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
            List<Map<String, Object>> transactionsResponse = (List<Map<String, Object>>) ((Map<String, Object>)
                    bodyResponse.get("transactions")).get("booked");
            List<Transaction> transactions = new ArrayList<>();

            for (var tran : transactionsResponse) {
                String transactionId = (String) tran.get("endToEndId");
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse((String) tran.get("bookingDate"));

                Transaction transaction = Transaction.builder()
                        .transactionId(transactionId)
                        .creditorName((String) tran.get("creditoame"))
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

    public String fetchPaymentStatus(Account account, String paymentId, String accessToken) {
        String url = "https://api.devbrd.ro/brd-api-connect-prod-organization/apicatalog/brd-psd2-pisp/v1/payments";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .addHeader("x-request-id", uuid.toString())
                .addHeader("psu-ip-address", "127.0.0.1")
                .addHeader("content-type", "application/xml")
                .url(url + "/" + paymentId + "/status")
                .build();

        Call call = client.newCall(request);
        try {
            Response response = call.execute();

            if (response.code() > 299) {
                response.close();
                return null;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(response.body().string()));
            Document doc = builder.parse(is);

            Element root = doc.getDocumentElement();

            return root.getElementsByTagName("PmtInfSts").item(0).getTextContent();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }
}
