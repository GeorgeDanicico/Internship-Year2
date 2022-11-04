package com.montran.internship.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "Payment")
@Table(name = "payment")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount")
    private String amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "date")
    private Date date;

    @Column(name = "creditor_name")
    private String creditorName;

    @Column(name = "debtor_name")
    private String debtorName;

    @Column(name = "creditor_iban")
    private String creditorIban;

    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    @JsonIgnore
    private Account account;

    @ManyToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    @JsonIgnore
    private Profile profile;

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        return ((Payment) o).getPaymentId().equals(this.paymentId);
    }
}
