package com.example.Social_Media.Service;

import com.example.Social_Media.Entity.Content;
import com.example.Social_Media.Entity.ContentLike;
import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Model.LikeEvent;
import com.example.Social_Media.Repository.ContentLikeRepository;
import com.example.Social_Media.Repository.ContentRepository;
import com.example.Social_Media.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;

@Service
@EnableScheduling
public class LikeEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(LikeEventConsumer.class);

    private static final String CONSUMER_GROUP = "like-processors";
    private static final String CONSUMER_NAME  = "worker-1";
    private static final long   BATCH_SIZE     = 100L;

    @Autowired private RedisTemplate<String, String> redisTemplate;
    @Autowired private ContentLikeRepository contentLikeRepository;
    @Autowired private ContentRepository contentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ObjectMapper objectMapper;

    // ── Create the consumer group once on startup ──────────────────────────

    @PostConstruct
    public void createConsumerGroup() {
        try {
            redisTemplate.opsForStream()
                    .createGroup(LikeQueueService.LIKE_STREAM, CONSUMER_GROUP);
            log.info("✅ Consumer group '{}' created", CONSUMER_GROUP);
        } catch (Exception e) {
            // Group already exists — that's fine
            log.info("ℹ️ Consumer group already exists");
        }
    }

    // ── Poll every 5 seconds, process up to 100 events ────────────────────

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processBatch() {
        try {
            List<MapRecord<String, Object, Object>> messages =
                    redisTemplate.opsForStream().read(
                            Consumer.from(CONSUMER_GROUP, CONSUMER_NAME),
                            StreamReadOptions.empty().count(BATCH_SIZE),
                            StreamOffset.create(
                                    LikeQueueService.LIKE_STREAM,
                                    ReadOffset.lastConsumed()
                            )
                    );

            if (messages == null || messages.isEmpty()) return;

            log.info("📦 Processing batch of {} like events", messages.size());

            // Deduplicate — last event per idempotency key wins
            // (handles rapid like/unlike/like in quick succession)
            Map<String, LikeEvent> deduped = new LinkedHashMap<>();
            for (MapRecord<String, Object, Object> msg : messages) {
                String payload = (String) msg.getValue().get("payload");
                LikeEvent event = objectMapper.readValue(payload, LikeEvent.class);
                // Later events overwrite earlier ones for the same user+content
                deduped.put(event.getIdempotencyKey(), event);
            }

            // Persist to DB in bulk
            persistBatch(new ArrayList<>(deduped.values()));

            // Acknowledge all messages (even the ones we deduped away)
            String[] ids = messages.stream()
                    .map(m -> m.getId().getValue())
                    .toArray(String[]::new);
            redisTemplate.opsForStream()
                    .acknowledge(LikeQueueService.LIKE_STREAM, CONSUMER_GROUP, ids);

            log.info("✅ Batch committed: {} unique events from {} raw messages",
                    deduped.size(), messages.size());

        } catch (Exception e) {
            // Messages stay unacknowledged — Redis redelivers on next poll
            log.error("❌ Batch processing failed — will retry", e);
        }
    }

    // ── Bulk DB write ──────────────────────────────────────────────────────

    private void persistBatch(List<LikeEvent> events) {
        List<ContentLike> toInsert = new ArrayList<>();
        List<long[]> toDelete = new ArrayList<>();  // [contentId, userId] pairs

        for (LikeEvent event : events) {
            if ("LIKE".equals(event.getAction())) {
                Content content = contentRepository.findById(event.getContentId()).orElse(null);
                User user = userRepository.findById(event.getUserId()).orElse(null);
                if (content == null || user == null) continue;

                // Skip if already liked in DB (idempotent insert)
                if (!contentLikeRepository.existsByContentAndUser(content, user)) {
                    ContentLike like = new ContentLike();
                    like.setContent(content);
                    like.setUser(user);
                    like.setCreatedAt(LocalDateTime.now());
                    toInsert.add(like);
                }

                // Sync the denormalized count on Content
                content.setLikes(contentLikeRepository.countByContentId(event.getContentId()).intValue() + toInsert.size());
                contentRepository.save(content);

            } else { // UNLIKE
                toDelete.add(new long[]{event.getContentId(), event.getUserId()});
            }
        }

        // Bulk insert likes
        if (!toInsert.isEmpty()) {
            contentLikeRepository.saveAll(toInsert);
            log.info("💾 Inserted {} likes", toInsert.size());
        }

        // Bulk delete unlikes
        for (long[] pair : toDelete) {
            contentLikeRepository.deleteByContentIdAndUserId(pair[0], pair[1]);
            // Sync count
            contentRepository.findById(pair[0]).ifPresent(content -> {
                long newCount = contentLikeRepository.countByContentId(pair[0]);
                content.setLikes((int) newCount);
                contentRepository.save(content);
            });
        }

        if (!toDelete.isEmpty()) {
            log.info("🗑️ Deleted {} unlikes", toDelete.size());
        }
    }
}