package com.montran.internship.config;

import com.montran.internship.model.JPAEntitiesPackageIndicator;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackageClasses = { JPAEntitiesPackageIndicator.class })
@EnableJpaRepositories(basePackages = "com.montran.internship.repository")
public class EntitiesConfig {
}
