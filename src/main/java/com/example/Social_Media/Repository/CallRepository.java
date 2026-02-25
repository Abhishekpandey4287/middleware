package com.example.Social_Media.Repository;

import com.example.Social_Media.Entity.Call;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CallRepository extends JpaRepository<Call, Long> {

    // FIXED: Find ongoing OR ringing call for a user with LIMIT 1
    @Query(value = "SELECT * FROM calls c WHERE (c.caller_id = :userId OR c.receiver_id = :userId) AND c.call_status IN ('ringing', 'ongoing') ORDER BY c.created_at DESC LIMIT 1", nativeQuery = true)
    Optional<Call> findOngoingCallForUser(@Param("userId") Long userId);

    // Find call by room ID
    Optional<Call> findByRoomId(String roomId);

    // Get call history for a user
    @Query("SELECT c FROM Call c WHERE (c.caller.id = :userId OR c.receiver.id = :userId) AND c.callStatus IN ('completed', 'missed', 'rejected', 'cancelled') ORDER BY c.createdAt DESC")
    List<Call> getCallHistory(@Param("userId") Long userId);

    // Get missed calls for a user
    @Query("SELECT c FROM Call c WHERE c.receiver.id = :userId AND c.callStatus = 'missed' ORDER BY c.createdAt DESC")
    List<Call> getMissedCalls(@Param("userId") Long userId);

    // Count missed calls
    @Query("SELECT COUNT(c) FROM Call c WHERE c.receiver.id = :userId AND c.callStatus = 'missed'")
    Long countMissedCalls(@Param("userId") Long userId);

    // Get calls between two users
    @Query("SELECT c FROM Call c WHERE ((c.caller.id = :userId1 AND c.receiver.id = :userId2) OR (c.caller.id = :userId2 AND c.receiver.id = :userId1)) ORDER BY c.createdAt DESC")
    List<Call> getCallsBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Get active calls (ringing or ongoing)
    @Query("SELECT c FROM Call c WHERE c.callStatus IN ('ringing', 'ongoing')")
    List<Call> getActiveCalls();

    // Get calls by date range
    @Query("SELECT c FROM Call c WHERE (c.caller.id = :userId OR c.receiver.id = :userId) AND c.createdAt BETWEEN :startDate AND :endDate ORDER BY c.createdAt DESC")
    List<Call> getCallsByDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}