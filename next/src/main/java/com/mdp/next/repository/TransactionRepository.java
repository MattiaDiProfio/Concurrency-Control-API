package com.mdp.next.repository;

import com.mdp.next.entity.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    @Query("""
        SELECT t FROM Transaction t
        WHERE t.validationId < :validationID
    """)
    List<Transaction> getRelevantSet(Long validationID);

}
