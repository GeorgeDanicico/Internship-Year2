package com.montran.internship.model;

import lombok.*;

import javax.persistence.*;

@Entity(name = "Balance")
@Table(name = "balance")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(
            name = "currency",
            columnDefinition = "varchar(10)",
            nullable = false
    )
    private String currency;

    @Column(
            name = "amount",
            nullable = false
    )
    private Double amount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;
}
