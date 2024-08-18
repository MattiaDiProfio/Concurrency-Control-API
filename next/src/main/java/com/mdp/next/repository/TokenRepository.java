package com.mdp.next.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.mdp.next.entity.Token;
import java.util.Optional;
import java.util.List;

public interface TokenRepository extends CrudRepository<Token, Long> {
    @Query("""
        SELECT t FROM Token t inner JOIN User u
        ON t.user.userId = u.userId
        WHERE t.user.userId = :userId and t.isActive = true
    """)
    List<Token> findAllTokenByUser(Long userId);
    Optional<Token> findByBody(String body);
}
