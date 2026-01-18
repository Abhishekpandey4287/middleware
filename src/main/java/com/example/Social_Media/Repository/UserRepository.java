package com.example.Social_Media.Repository;

import com.example.Social_Media.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Search users by name
    List<User> findByNameContainingIgnoreCase(String name);

    // Find users by role
    List<User> findByRole(String role);
}