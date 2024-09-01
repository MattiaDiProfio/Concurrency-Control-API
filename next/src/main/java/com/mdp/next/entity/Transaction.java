package com.mdp.next.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import jakarta.persistence.*;
import java.util.List;
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

    // since the transaction's sender and receiver are excluded by the JsonIgnore
    // explicitly show the accountId of the sender and receiver, this will be removed
    // once testing is completed!
    @NonNull
    @Column(name = "sender_account_id", nullable = false)
    private Long senderAccountId;

    @NonNull
    @Column(name = "receiver_account_id", nullable = false)
    private Long receiverAccountId;

    @ManyToOne
    @JoinColumn(name = "sender_account", referencedColumnName = "account_id")
    @JsonIgnore
    private Account sender;

    @ManyToOne
    @JoinColumn(name = "receiver_account", referencedColumnName = "account_id")
    @JsonIgnore
    private Account receiver;

    // >>>>>>>>>>>>>>>>>>>>>>>> OCC related fields >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    @Column(name = "validation_id")
    @JsonIgnore
    private Long validationId;

    @Column(name = "starttime")
    @JsonIgnore
    private LocalDateTime transactionStartTimeStamp;

    @Column(name = "working_phase_endtime")
    @JsonIgnore
    private LocalDateTime transactionEndWorkingPhaseTimeStamp;

    @Column(name = "validation_phase_endtime")
    @JsonIgnore
    private LocalDateTime transactionEndValidationPhaseTimeStamp;

    @Column(name = "endtime")
    @JsonIgnore
    private LocalDateTime transactionEndTimeStamp;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "transaction_read_set",
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private List<Account> readSet;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "transaction_write_set",
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private List<Account> writeSet;

}
