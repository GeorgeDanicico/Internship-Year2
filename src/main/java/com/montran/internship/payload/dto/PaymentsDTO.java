package com.montran.internship.payload.dto;

import com.montran.internship.model.Payment;

import java.util.List;

public class PaymentsDTO {
    List<PaymentDTO> payments;

    public PaymentsDTO(List<PaymentDTO> payments) {
        this.payments = payments;
    }

    public List<PaymentDTO> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentDTO> payments) {
        this.payments = payments;
    }
}
