package com.example.Social_Media.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "investment")
public class Investment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne private User investor;
    @ManyToOne private User creator;
    private Double amount;
    private String status; // active/closed
    private LocalDateTime createdAt;
}
