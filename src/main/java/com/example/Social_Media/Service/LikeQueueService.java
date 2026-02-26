package com.example.Social_Media.Service;

import com.example.Social_Media.Model.LikeEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class LikeQueueService {

    // Redis key constants
    public static final String LIKE_STREAM       = "likes:stream";
    public static final String COUNT_PREFIX      = "likes:count:";
    public static final String USER_LIKED_PREFIX = "likes:user:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 1. Updates Redis cache immediately (instant reads).
     * 2. Pushes a LikeEvent onto the Redis stream for async DB write.
     * Returns the new optimistic like state.
     */
    public LikeEvent enqueue(Long contentId, Long userId) throws Exception {
        String countKey     = COUNT_PREFIX + contentId;
        String userLikedKey = USER_LIKED_PREFIX + userId + ":" + contentId;

        boolean isCurrentlyLiked = Boolean.TRUE.equals(redisTemplate.hasKey(userLikedKey));
        String action = isCurrentlyLiked ? "UNLIKE" : "LIKE";

        // Update cache atomically
        if ("LIKE".equals(action)) {
            redisTemplate.opsForValue().increment(countKey);
            redisTemplate.opsForValue().set(userLikedKey, "1", Duration.ofDays(7));
        } else {
            redisTemplate.opsForValue().decrement(countKey);
            redisTemplate.delete(userLikedKey);
        }

        // Push event to stream for the consumer worker to pick up
        LikeEvent event = new LikeEvent(contentId, userId, action);
        Map<String, String> message = Map.of(
                "payload", objectMapper.writeValueAsString(event)
        );
        redisTemplate.opsForStream().add(LIKE_STREAM, message);

        return event;
    }

    public boolean isLikedByUser(Long contentId, Long userId) {
        // Check Redis first (fast), DB fallback handled in the consumer
        String key = USER_LIKED_PREFIX + userId + ":" + contentId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public int getLikeCount(Long contentId) {
        String key = COUNT_PREFIX + contentId;
        String val = redisTemplate.opsForValue().get(key);
        return val != null ? Integer.parseInt(val) : 0;
    }

    /**
     * Called on app startup to warm up Redis from DB counts.
     * Prevents returning 0 on first request after a restart.
     */
    public void warmUpCache(Long contentId, int dbCount) {
        String key = COUNT_PREFIX + contentId;
        // Only set if not already in cache
        redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(dbCount));
    }

    public void warmUpUserLike(Long contentId, Long userId) {
        String key = USER_LIKED_PREFIX + userId + ":" + contentId;
        redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofDays(7));
    }
}