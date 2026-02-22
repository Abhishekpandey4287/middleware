package com.example.Social_Media.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user1_id")
    private User user1;  // First user in conversation

    @ManyToOne
    @JoinColumn(name = "user2_id")
    private User user2;  // Second user in conversation

    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;

    @Column(name = "last_message_sender_id")
    private Long lastMessageSenderId;

    @Column(name = "user1_unread_count")
    private Integer user1UnreadCount = 0;

    @Column(name = "user2_unread_count")
    private Integer user2UnreadCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}