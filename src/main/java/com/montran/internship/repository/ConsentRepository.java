package com.montran.internship.repository;

import com.montran.internship.model.Bank;
import com.montran.internship.model.Consent;
import com.montran.internship.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {
    Optional<Consent> findByBankAndProfile(Bank bank, Profile profile);
    Optional<Consent> findByConsentId(String consentId);
    Optional<Consent> findByConsentIdAndProfile(String consentId, Profile profile);

}
