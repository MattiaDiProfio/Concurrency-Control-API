package com.mdp.next.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import com.mdp.next.entity.Account;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Optional<Account> findByUserID(Long userID);
}
