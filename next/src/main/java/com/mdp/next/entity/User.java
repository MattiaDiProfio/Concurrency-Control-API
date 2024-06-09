package com.mdp.next.entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mdp.next.exception.Role;
import jakarta.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long ID;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "ID")
    private Account account;

    @NonNull
    @NotBlank(message = "User's name cannot be blank")
    @Column(name = "full_name", nullable = false)
    private String name;

    @NonNull
    @Email(message = "User's email must follow a valid email format")
    @Column(name = "email_address", nullable = false, unique = true)
    private String email;

    @NonNull
    @NotBlank(message = "User's address cannot be blank")
    @Column(name = "address", nullable = false)
    private String address;

	@NotBlank(message =  "username cannot be blank")
	@NonNull
	@Column(nullable = false, unique = true)
	private String username;

	@NotBlank(message =  "password cannot be blank")
    @NonNull
	@Column(nullable = false)
	private String password;

    @NotBlank(message = "user role cannot be blank")
    @NonNull
    @Role
    @Column(nullable = false)
    private String role;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Token> tokens = new ArrayList<>();

}
