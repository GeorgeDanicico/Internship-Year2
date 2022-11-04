package com.montran.internship.repository;

import com.montran.internship.model.Account;
import com.montran.internship.model.Payment;
import com.montran.internship.model.Profile;
import com.montran.internship.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentId);
    Optional<Payment> findByPaymentIdAndProfile(String paymentId, Profile profile);
    Optional<Payment> findByPaymentIdAndAccount(String paymentId, Account account);
    List<Payment> findAllByAccount(Account account);
}
