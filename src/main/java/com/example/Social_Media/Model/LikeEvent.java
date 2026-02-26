package com.example.Social_Media.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeEvent {
    private Long contentId;
    private Long userId;
    private String action;
    private long timestamp;
    private String idempotencyKey;

    public LikeEvent(Long contentId, Long userId, String action) {
        this.contentId = contentId;
        this.userId = userId;
        this.action = action;
        this.timestamp = System.currentTimeMillis();
        this.idempotencyKey = userId + ":" + contentId;
    }
}