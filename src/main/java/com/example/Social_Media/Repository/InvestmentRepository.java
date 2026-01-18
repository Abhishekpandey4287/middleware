package com.example.Social_Media.Repository;

import com.example.Social_Media.Entity.Investment;
import com.example.Social_Media.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    // Find investments by investor
    List<Investment> findByInvestor(User investor);

    // Find investments by creator
    List<Investment> findByCreator(User creator);

    // Find active investments
    List<Investment> findByStatus(String status);

    // Find investments by investor ID
    List<Investment> findByInvestorId(Long investorId);

    // Find investments by creator ID
    List<Investment> findByCreatorId(Long creatorId);
}

