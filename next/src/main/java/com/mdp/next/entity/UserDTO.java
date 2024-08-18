package com.mdp.next.entity;

import lombok.Getter;
import lombok.Setter;
// the purpose of this class is to act as a User class replacement for user objects to be 
// sent back in response bodies, for example in GET/users/all and GET/user/userID.
// by using this class, we can pick the fields we want to omit from the response, in this case the hashed password
// note that we fetching accounts, transactions, etc. only the user ID is serialised into the response, so there 
// is no need to use a DTO in such cases.

@Getter
@Setter
public class UserDTO {

    private Long ID;
    private String name;
    private String email;
    private String address;
	private String username;
    private String role;

    public UserDTO (User user) {
        setID(user.getUserId());
        setName(user.getName());
        setEmail(user.getEmail());
        setAddress(user.getAddress());
        setUsername(user.getUsername());
        setRole(user.getRole());
    }
}
