package com.example.Social_Media.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "calls")
public class Call {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "caller_id")
    private User caller;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(name = "call_type")
    private String callType; // "video" or "audio"

    @Column(name = "call_status")
    private String callStatus; // "initiated", "ringing", "ongoing", "completed", "missed", "rejected", "cancelled"

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration")
    private Long duration; // in seconds

    @Column(name = "room_id")
    private String roomId; // WebRTC room identifier

    @Column(name = "is_group_call")
    private Boolean isGroupCall = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper method to calculate duration
    public void calculateDuration() {
        if (startTime != null && endTime != null) {
            duration = java.time.Duration.between(startTime, endTime).getSeconds();
        }
    }
}