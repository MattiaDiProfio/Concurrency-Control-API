package com.mdp.next.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @Column(name = "id")
    private Long ID;

    @NonNull
    @Column(name = "amount", nullable = false)
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "ID")
    @JsonIgnore
    private Account sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "ID")
    @JsonIgnore
    private Account receiver;

    @NonNull
    private Long receiverID;

    @NonNull
    private Long senderID;

    @Column(name = "created_at", nullable = false)
    private String createdAt; 

    @PrePersist // populate the field with the current time whenever a transaction is instantiated
    protected void onCreate() {
        createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // concurrency managements fields 
    @ElementCollection
    @JsonIgnore
    private List<Account> readSet = new ArrayList<>(); // holds the Account objects the transaction will read from

    @ElementCollection
    @JsonIgnore
    private List<Account> writeSet = new ArrayList<>(); // holds the Account objects the transactions will write to

    @Column(name = "phase")
    @JsonIgnore
    private String currPhase = "WORKING"; // by default, when a transaction is opened, it enters the working phase

    public void addReadObject(Account account) {
        this.readSet.add(account);
    }

    public void addWriteObject(Account account) {
        this.writeSet.add(account);
    }

    public void emptyReadWriteSets() {
        this.writeSet.clear();
        this.readSet.clear();
    }

    public boolean isYoungerThan(Transaction t) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime a = LocalDateTime.parse(this.getCreatedAt(), formatter);
        LocalDateTime b = LocalDateTime.parse(t.getCreatedAt(), formatter);
        return a.isBefore(b);
    }

}
