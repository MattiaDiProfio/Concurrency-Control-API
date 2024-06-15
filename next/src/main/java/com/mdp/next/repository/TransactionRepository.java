package com.mdp.next.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.mdp.next.entity.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    @Query("""
        SELECT t FROM Transaction t 
        WHERE t.currPhase = 'WORKING' AND t.createdAt < :currTime
    """)
    List<Transaction> getOverlappingTransactions(String currTime);

}
