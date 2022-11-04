package com.montran.internship.service;

import com.montran.internship.model.Consent;
import com.montran.internship.model.Payment;
import com.montran.internship.model.Profile;
import com.montran.internship.payload.dto.PaymentAuthDTO;
import com.montran.internship.payload.dto.PaymentDTO;
import com.montran.internship.payload.request.PaymentRequest;

import java.util.List;

public interface PaymentService {
    PaymentAuthDTO submitPayment(Profile profile, PaymentRequest paymentRequest);
    List<PaymentDTO> getAllPayments(Profile profile, String accountId);
    void updatePaymentStatus(Consent consent, String accessToken, String paymentId);
}
