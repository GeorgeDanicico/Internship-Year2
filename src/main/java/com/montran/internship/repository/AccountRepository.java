package com.montran.internship.repository;

import com.montran.internship.model.Account;
import com.montran.internship.model.Bank;
import com.montran.internship.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByResourceId(String resourceId);
    Optional<Account> findByResourceIdAndProfile(String resourceId, Profile profile);
    Optional<Account> findByIbanAndProfile(String iban, Profile profile);
    List<Account> findAllByBankAndProfile(Bank bank, Profile profile);
    List<Account> findAllByProfile(Profile profile);
}
