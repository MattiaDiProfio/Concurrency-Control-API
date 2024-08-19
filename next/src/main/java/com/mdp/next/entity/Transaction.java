package com.mdp.next.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import jakarta.persistence.*;
import java.util.HashSet;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "transaction_id")
    private Long transactionId;

    @NonNull
    @Column(name = "amount", nullable = false)
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "sender_account", referencedColumnName = "account_id")
    @JsonIgnore
    private Account sender;

    @ManyToOne
    @JoinColumn(name = "receiver_account", referencedColumnName = "account_id")
    @JsonIgnore
    private Account receiver;

    @Column(name = "validation_id")
    private Long validationId;

    @Column(name = "starttime")
    private LocalDateTime transactionStartTimeStamp;

    @Column(name = "working_phase_endtime")
    private LocalDateTime transactionEndWorkingPhaseTimeStamp;

    @Column(name = "validation_phase_endtime")
    private LocalDateTime transactionEndValidationPhaseTimeStamp;

    @Column(name = "endtime")
    private LocalDateTime transactionEndTimeStamp;

    @ManyToMany
    @JoinTable(
            name = "transaction_read_set",
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private HashSet<Account> readSet;

    @ManyToMany
    @JoinTable(
            name = "transaction_write_set",
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private HashSet<Account> writeSet;

    // since the transaction's sender and receiver are excluded by the JsonIgnore
    // explicitly show the accountId of the sender and receiver, this will be removed
    // once testing is completed!
    @Column(name = "sender_account_id")
    private Long senderAccountId;

    @Column(name = "receiver_account_id")
    private Long receiverAccountId;
}
