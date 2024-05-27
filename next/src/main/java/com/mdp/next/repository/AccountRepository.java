package com.mdp.next.repository;

import org.springframework.data.repository.CrudRepository;
import com.mdp.next.entity.Account;

public interface AccountRepository extends CrudRepository<Account, String> {
    
}
