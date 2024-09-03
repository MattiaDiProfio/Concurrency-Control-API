package com.mdp.next.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mdp.next.exception.Type;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "type", nullable = false)
    @NonNull
    @Type // use the custom-defined 'Type' annotation
    private String type;

    @Column(name = "balance", nullable = false)
    private Double balance = 0.0;

    // we do not cascade the deletion operation since we want to keep a log of
    // all transactions handled by the API (***)
    @OneToMany(mappedBy = "sender")
    private List<Transaction> sentTransactions = new ArrayList<>();

    // same as (***)
    @OneToMany(mappedBy = "receiver")
    private List<Transaction> receivedTransactions = new ArrayList<>();

    // since accountOwner is excluded by JsonIgnore, explicitly show the account owner user ID for development purposes
    @Column(name = "account_owner_id")
    private Long accountOwnerId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "account_owner", referencedColumnName = "user_id")
    private User accountOwner;

}
