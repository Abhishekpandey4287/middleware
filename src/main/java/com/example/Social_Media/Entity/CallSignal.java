package com.example.Social_Media.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "call_signals")
public class CallSignal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id")
    private String roomId;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(name = "signal_type")
    private String signalType; // "offer", "answer", "ice-candidate", "hang-up"

    @Column(name = "signal_data", columnDefinition = "TEXT")
    private String signalData; // JSON data for WebRTC signals

    @Column(name = "is_processed")
    private Boolean isProcessed = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}