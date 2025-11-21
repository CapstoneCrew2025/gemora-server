package com.gemora_server.repo;

import com.gemora_server.entity.ChatMessage;
import com.gemora_server.enums.ChatMessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepo extends JpaRepository<ChatMessage, Long> {

    @Query("""
        SELECT m.roomId AS roomId, MAX(m.sentAt) AS lastAt
        FROM ChatMessage m
        WHERE m.senderId = :userId OR m.receiverId = :userId
        GROUP BY m.roomId
        ORDER BY MAX(m.sentAt) DESC
    """)

    List<Object[]> findRoomIdAndLastAtByUser(@Param("userId") Long userId);

    List<ChatMessage> findByRoomIdOrderBySentAtAsc(String roomId);

    ChatMessage findTopByRoomIdOrderBySentAtDesc(String roomId);

    Long countByRoomIdAndReceiverIdAndStatus(String roomId, Long receiverId, ChatMessageStatus status);
}
