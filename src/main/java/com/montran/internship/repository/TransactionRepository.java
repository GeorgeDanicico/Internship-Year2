package com.montran.internship.repository;

import com.montran.internship.model.Account;
import com.montran.internship.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(String transactionId);
    Optional<Transaction> findByTransactionIdAndAccount(String transactionId, Account account);
    List<Transaction> findByAccountAndDateBetween(Account account, Date dateStart, Date dateEnd);
}
