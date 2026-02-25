package com.example.Social_Media.Repository;

import com.example.Social_Media.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find by email
    Optional<User> findByEmail(String email);

    // Find by username
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<User> findByNameContainingIgnoreCase(String name);

    List<User> findByUsernameContainingIgnoreCase(String username);

    List<User> findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(String name, String username);

    List<User> findByRole(String role);
}