package com.mdp.next.entity;

import java.time.LocalDateTime;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String ID;

    @NonNull
    @Column(name = "amount", nullable = false)
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "ID")
    private Account sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "ID")
    private Account receiver;

    @NonNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; 

}
