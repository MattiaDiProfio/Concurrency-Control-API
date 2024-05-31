package com.mdp.next.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.*;
import jakarta.persistence.*;

@Getter
@Setter
@AllArgsConstructor
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
    private Account sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "ID")
    private Account receiver;

    private Long receiverID;
    private Long senderID;

    @Column(name = "created_at", nullable = false)
    private String createdAt; 

    @PrePersist // populate the field with the current time whenever a transaction is instantiated
    protected void onCreate() {
        createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
