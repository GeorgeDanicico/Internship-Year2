package com.montran.internship.repository;

import com.montran.internship.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    public Optional<Bank> findByBankName(String bankName);
}
