package com.montran.internship.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "Transaction")
@Table(name = "transaction")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount")
    private String amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "date")
    private Date date;

    @Column(name = "creditor_name")
    private String creditorName;

    @Column(name = "debtor_name")
    private String debtorName;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    @JsonIgnore
    private Account account;

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        return ((Transaction) o).getTransactionId().equals(this.transactionId);
    }

}
