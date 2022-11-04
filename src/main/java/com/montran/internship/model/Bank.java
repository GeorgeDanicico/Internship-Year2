package com.montran.internship.model;

import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "Banks")
@Table(name = "banks")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(
            name = "bank_name",
            nullable = false
    )
    private String bankName;

    @OneToMany(mappedBy = "bank")
    private Set<Consent> consents;

    @OneToMany(mappedBy="bank")
    private Set<Account> accounts;
}
