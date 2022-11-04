package com.montran.internship.service.impl;

import com.montran.internship.model.*;
import com.montran.internship.model.enums.Banks;
import com.montran.internship.payload.dto.PaymentAuthDTO;
import com.montran.internship.payload.dto.PaymentDTO;
import com.montran.internship.payload.request.PaymentRequest;
import com.montran.internship.repository.AccountRepository;
import com.montran.internship.repository.ConsentRepository;
import com.montran.internship.repository.PaymentRepository;
import com.montran.internship.service.PaymentService;
import com.montran.internship.service.bankcalls.BRDBankCall;
import com.montran.internship.service.bankcalls.BTBankCall;
import com.montran.internship.service.bankcalls.CecBankCall;
import com.montran.internship.util.CECUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BTBankCall btBankCall;
    @Autowired
    private BRDBankCall brdBankCall;
    @Autowired
    private CecBankCall cecBankCall;
    @Autowired
    private ConsentRepository consentRepository;

    private void savePayment(Account account, String paymentId, String paymentStatus, PaymentRequest paymentRequest) {
        try {
            Payment payment = Payment.builder()
                    .paymentId(paymentId)
                    .creditorName(paymentRequest.getCreditorName())
                    .debtorName(account.getFullName())
                    .date(new SimpleDateFormat("yyyy-MM-dd").parse(paymentRequest.getPaymentDate()))
                    .amount(String.valueOf(paymentRequest.getPaymentAmount()))
                    .account(account)
                    .currency(account.getBalance().getCurrency())
                    .profile(account.getProfile())
                    .creditorIban(paymentRequest.getCreditorIban())
                    .status(paymentStatus)
                    .build();

            this.paymentRepository.save(payment);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private PaymentAuthDTO submitBTPayment(Account account, Consent consent, PaymentRequest paymentRequest) {
        Map<String, String> info = btBankCall.payment(account, paymentRequest);

        if (info == null) {
            return null;
        }

        if (account.getBalance().getAmount() < paymentRequest.getPaymentAmount()) {
            return new PaymentAuthDTO(null, null, "Insufficient funds");
        }

        final String url = "https://apistorebt.ro/auth/realms/psd2-sb/protocol/openid-connect/auth?";
        final String redirectUri = "https://localhost:3000/success-payment";
        String paymentId = info.get("paymentId");

        this.savePayment(account, paymentId, info.get("paymentStatus"), paymentRequest);

        return new PaymentAuthDTO(paymentId, btBankCall.generateRedirectLink(url, redirectUri, consent.getClientId(), paymentId, "PIS"));
    }

    private PaymentAuthDTO submitBRDPayment(Account account, Consent consent, PaymentRequest paymentRequest) {
        if (account.getBalance().getAmount() < paymentRequest.getPaymentAmount()) {
            return new PaymentAuthDTO(null, null, "Insufficient funds");
        }

        Map<String, String> info = brdBankCall.payment(consent.getProfile(), account, paymentRequest);

        if (info == null) {
            return null;
        }

        this.savePayment(account, info.get("paymentId"), info.get("paymentStatus"), paymentRequest);

        return new PaymentAuthDTO(info.get("paymentId"), null);
    }

    private PaymentAuthDTO submitCECPayment(Account account, Consent consent, PaymentRequest paymentRequest) {
        Map<String, String> info = cecBankCall.payment(account, paymentRequest);

        if (info == null) {
            return null;
        }

        if (account.getBalance().getAmount() < paymentRequest.getPaymentAmount()) {
            return new PaymentAuthDTO(null, null, "Insufficient funds");
        }

        final String url = CECUtil.oauthLink();
        final String redirectUri = CECUtil.paymentRedirect();
        String paymentId = info.get("paymentId");

        this.savePayment(account, paymentId, info.get("paymentStatus"), paymentRequest);

        return new PaymentAuthDTO(paymentId, cecBankCall.generateRedirectLink(url, redirectUri, consent.getClientId(), paymentId, "PIS"));
    }

    @Override
    public PaymentAuthDTO submitPayment(Profile profile, PaymentRequest paymentRequest) {

        String debtorAccountId = paymentRequest.getDebtorAccountId();
        Account debtorAccount = accountRepository.findByResourceIdAndProfile(debtorAccountId, profile).orElse(null);

        if (debtorAccount == null) return null;

        Consent consent = consentRepository.findByBankAndProfile(debtorAccount.getBank(), profile).orElse(null);

        if (debtorAccount.getBank().getBankName().equals(Banks.BT.label)) {
            return submitBTPayment(debtorAccount, consent, paymentRequest);
        }

        if (debtorAccount.getBank().getBankName().equals(Banks.BRD.label)) {
            return submitBRDPayment(debtorAccount, consent, paymentRequest);
        }

        if (debtorAccount.getBank().getBankName().equals(Banks.CEC.label)) {
            return submitCECPayment(debtorAccount, consent, paymentRequest);
        }

        return null;
    }

    @Override
    public List<PaymentDTO> getAllPayments(Profile profile, String accountId) {

        Account account = accountRepository.findByResourceIdAndProfile(accountId, profile).orElse(null);

        if (account == null) {
            return null;
        }

        List<Payment> payments = account.getPayments().stream().toList();


        return payments.stream()
                .map((payment) -> new PaymentDTO(
                        account.getBalance().getCurrency(),
                        payment.getCreditorName(),
                        payment.getDate(),
                        Double.parseDouble(payment.getAmount()),
                        payment.getPaymentId(),
                        payment.getCreditorIban(),
                        payment.getStatus()
                ))
                .toList();
    }

    @Override
    public void updatePaymentStatus(Consent consent, String accessToken, String paymentId) {
        Payment payment = paymentRepository.findByPaymentIdAndProfile(paymentId, consent.getProfile()).orElse(null);

        if (payment != null) {
            String paymentStatus = "";
            if (consent.getBank().getBankName().equals(Banks.BT.label)) {
                paymentStatus = btBankCall.fetchPaymentStatus(payment.getAccount(), payment.getPaymentId(), accessToken);
            }
            if (consent.getBank().getBankName().equals(Banks.BRD.label)) {
                paymentStatus = brdBankCall.fetchPaymentStatus(payment.getAccount(), payment.getPaymentId(), accessToken);
            }
            if (consent.getBank().getBankName().equals(Banks.CEC.label)) {
                paymentStatus = cecBankCall.fetchPaymentStatus(consent, payment.getAccount(), payment.getPaymentId(), accessToken);
            }
            payment.setStatus(paymentStatus);

            paymentRepository.save(payment);
        }
    }
}
