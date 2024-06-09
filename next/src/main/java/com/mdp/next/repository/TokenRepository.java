package com.mdp.next.repository;

import org.springframework.data.repository.CrudRepository;
import com.mdp.next.entity.Token;

public interface TokenRepository extends CrudRepository<Token, Long> {
    
}
