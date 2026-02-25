package com.example.Social_Media.Repository;

import com.example.Social_Media.Entity.WalletTransaction;
import com.example.Social_Media.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WalletRepository extends JpaRepository<WalletTransaction, Long> {

    // Find transactions by user
    List<WalletTransaction> findByUser(User user);

    // Find transactions by user ID
    List<WalletTransaction> findByUserId(Long userId);

    // Find transactions by type
    List<WalletTransaction> findByTxnType(String txnType);

    // Calculate balance for a user
    @Query("SELECT COALESCE(SUM(CASE WHEN w.txnType = 'credit' THEN w.amount ELSE -w.amount END), 0) " +
            "FROM WalletTransaction w WHERE w.user.id = :userId AND w.status = 'success'")
    Double calculateBalance(@Param("userId") Long userId);
}