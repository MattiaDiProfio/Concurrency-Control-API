package com.mdp.next.entity;

import lombok.*;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String ID;

    @NonNull
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "ID")
    private Account account;

    @NonNull
    @Column(name = "full_name", nullable = false)
    private String name;

    @NonNull
    @Column(name = "email_address", nullable = false, unique = true)
    private String email;

    @NonNull
    @Column(name = "address", nullable = false)
    private String address;

}
