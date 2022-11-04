package com.montran.internship.model;

import lombok.*;

import javax.persistence.*;

@Entity(name = "Consents")
@Table(name = "consents")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Consent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(
            name = "consent_id",
            nullable = false
    )
    private String consentId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret")
    private String clientSecret;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "bank_id", referencedColumnName = "id")
    private Bank bank;
}
