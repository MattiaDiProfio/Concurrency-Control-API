package com.mdp.next.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import jakarta.persistence.*;
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
    private String type;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Transaction> sentTransactions;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Transaction> receivedTransactions;

    // since accountOwner is excluded by JsonIgnore, explicitly show the account owner user ID for development purposes
    // this field will be removed once testing is done!
    @Column(name = "account_owner_id")
    private Long accountOwnerId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "account_owner", referencedColumnName = "user_id")
    private User accountOwner;

}
