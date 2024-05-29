package com.mdp.next.repository;

import org.springframework.data.repository.CrudRepository;
import com.mdp.next.entity.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    
}
