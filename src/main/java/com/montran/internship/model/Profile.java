package com.montran.internship.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "Profile")
@Table(
        name = "profile",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_profile_email",
                        columnNames = "email"
                ),
                @UniqueConstraint(
                        name = "uk_profile_username",
                        columnNames = "username"
                )
        }
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(
            name = "username",
            columnDefinition = "varchar(50)",
            nullable = false
    )
    private String username;

    @Column(
            name = "email",
            columnDefinition = "varchar(50)",
            nullable = false
    )
    private String email;

    @Column(
            name = "first_name",
            columnDefinition = "varchar(50)",
            nullable = false
    )
    private String firstName;

    @Column(
            name = "last_name",
            columnDefinition = "varchar(50)",
            nullable = false
    )
    private String lastName;

    @Column(
            name = "personal_numerical_code",
            columnDefinition = "char(13)",
            nullable = false
    )
    private String personalNumericalCode;

    @Column(
            name = "password",
            columnDefinition = "varchar(200)",
            nullable = false
    )
    private String password;

    @OneToMany(mappedBy = "profile")
    private Set<Account> accounts;

    @OneToMany(mappedBy = "profile")
    private Set<Consent> consents;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public Profile(
            String username,
            String email,
            String firstName,
            String lastName,
            String personalNumericalCode,
            String password,
            Set<Role> roles
    ) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalNumericalCode = personalNumericalCode;
        this.password = password;
        this.roles = roles;
    }
}
