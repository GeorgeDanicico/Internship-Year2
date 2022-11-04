package com.montran.internship.repository;

import com.montran.internship.model.Account;
import com.montran.internship.model.Bank;
import com.montran.internship.model.Profile;
import com.montran.internship.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByBankAndProfile(Bank bank, Profile profile);
    Optional<Token> findByRefreshToken(String refreshToken);
}
