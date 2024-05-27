package com.mdp.next.repository;

import org.springframework.data.repository.CrudRepository;
import com.mdp.next.entity.User;

public interface UserRepository extends CrudRepository<User, String> {
    
}
