package com.montran.internship.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "Account")
@Table(name = "account")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(
            name = "resourceId",
            nullable = false
    )
    private String resourceId;

    @Column(
            name = "name",
            nullable = false
    )
    private String fullName;

    @Column(
            name = "iban",
            columnDefinition = "varchar(50)",
            nullable = false
    )
    private String iban;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "account")
    private Balance balance;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "bank_id", referencedColumnName = "id")
    private Bank bank;

    @ManyToOne
    @JoinColumn(name = "refresh_token_id", referencedColumnName = "id")
    private Token token;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account")
    @JsonBackReference
    @ToString.Exclude
    private Set<Transaction> transactions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account")
    @JsonBackReference
    @ToString.Exclude
    private Set<Payment> payments;
}
