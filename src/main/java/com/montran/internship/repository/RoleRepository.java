package com.montran.internship.repository;
import java.util.Optional;

import com.montran.internship.model.Role;
import com.montran.internship.model.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
