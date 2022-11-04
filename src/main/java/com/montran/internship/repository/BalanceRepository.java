package com.montran.internship.repository;

import com.montran.internship.model.Account;
import com.montran.internship.model.Balance;
import com.montran.internship.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByAccount(Account account);
}
