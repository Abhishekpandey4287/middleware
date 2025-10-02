package com.example.Social_Media.Repository;

import com.example.Social_Media.Entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<WalletTransaction, Long> {
}
