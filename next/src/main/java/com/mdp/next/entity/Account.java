package com.mdp.next.entity;

import java.util.List;
import java.util.ArrayList;
import lombok.*;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long ID;

    @JsonIgnore
    @OneToOne(mappedBy = "account")
    private User user;

    @NonNull
    @Column(name = "balance", nullable = false)
    private Double balance;

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Transaction> sent = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Transaction> received = new ArrayList<>();

    // field for debug purpose only
    @Column(name = "user_id", unique = true)
    private Long userID;


    public void addSentTransaction(Transaction transaction) {
        sent.add(transaction);
    }

    public void addReceivedTransaction(Transaction transaction) {
        received.add(transaction);
    }

}
