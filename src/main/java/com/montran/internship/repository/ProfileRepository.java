package com.montran.internship.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.montran.internship.model.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUsername(String username);
    Boolean existsByUsername(String username);
    Optional<Profile> findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByPersonalNumericalCode(String personalNumericalCode);
    Optional<Profile> findByUsernameOrEmail(String username, String email);
    List<Profile> findProfilesByUsernameOrEmail(String username, String email);
}
