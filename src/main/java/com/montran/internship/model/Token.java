package com.montran.internship.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "Token")
@Table(name = "token")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "refresh_token")
    private String refreshToken;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Profile profile;

    @ManyToOne()
    @JoinColumn(name = "bank_id", referencedColumnName = "id")
    private Bank bank;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "token")
    private Set<Account> accounts;
}
