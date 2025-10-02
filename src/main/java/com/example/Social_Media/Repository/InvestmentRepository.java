package com.example.Social_Media.Repository;

import com.example.Social_Media.Entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {
}

