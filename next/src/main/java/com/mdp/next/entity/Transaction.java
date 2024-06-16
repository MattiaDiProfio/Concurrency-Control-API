package com.mdp.next.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.*;
import jakarta.persistence.*;
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

    @Column(name = "phase")
    @JsonIgnore
    private String currPhase = "WORKING"; // by default, when a transaction is opened, it enters the working phase

    public boolean isYoungerThan(Transaction t) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime a = LocalDateTime.parse(this.getCreatedAt(), formatter);
        LocalDateTime b = LocalDateTime.parse(t.getCreatedAt(), formatter);
        return a.isBefore(b);
    }

}
