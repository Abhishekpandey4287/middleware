package com.example.Social_Media.Repository;

import com.example.Social_Media.Entity.CallSignal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CallSignalRepository extends JpaRepository<CallSignal, Long> {

    // Get unprocessed signals for a user
    @Query("SELECT cs FROM CallSignal cs WHERE cs.receiver.id = :userId AND cs.isProcessed = false ORDER BY cs.createdAt ASC")
    List<CallSignal> getUnprocessedSignals(@Param("userId") Long userId);

    // Get signals for a room
    @Query("SELECT cs FROM CallSignal cs WHERE cs.roomId = :roomId ORDER BY cs.createdAt ASC")
    List<CallSignal> getSignalsByRoom(@Param("roomId") String roomId);

    // Get unprocessed signals for a specific room and receiver
    @Query("SELECT cs FROM CallSignal cs WHERE cs.roomId = :roomId AND cs.receiver.id = :receiverId AND cs.isProcessed = false ORDER BY cs.createdAt ASC")
    List<CallSignal> getUnprocessedSignalsForRoom(@Param("roomId") String roomId, @Param("receiverId") Long receiverId);

    // Delete old processed signals (cleanup)
    @Query("DELETE FROM CallSignal cs WHERE cs.isProcessed = true AND cs.createdAt < :cutoffDate")
    void deleteOldProcessedSignals(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
}