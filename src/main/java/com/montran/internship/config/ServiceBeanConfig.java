package com.montran.internship.config;

import com.montran.internship.service.*;
import com.montran.internship.service.bankcalls.BRDBankCall;
import com.montran.internship.service.bankcalls.BTBankCall;
import com.montran.internship.service.bankcalls.CecBankCall;
import com.montran.internship.service.impl.*;
import com.montran.internship.service.oauth.AuthorizationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceBeanConfig {
    @Bean
    public ConsentService consentService() {
        return new ConsentServiceImpl();
    }

    @Bean
    public AccountService accountService() {
        return new AccountServiceImpl();
    }

    @Bean
    public ProfileService profileService() {
        return new ProfileServiceImpl();
    }

    @Bean
    public BTBankCall btBankCall() { return new BTBankCall(); }

    @Bean
    public BRDBankCall brdBankCall() { return new BRDBankCall(); }

    @Bean
    public CecBankCall cecBankCall() { return new CecBankCall(); }

    @Bean
    public AuthorizationService authorizationService() { return new AuthorizationService(); }

    @Bean
    public TransactionService transactionService() { return new TransactionServiceImpl(); }

    @Bean
    public PaymentService paymentService() { return new PaymentServiceImpl(); }
}
