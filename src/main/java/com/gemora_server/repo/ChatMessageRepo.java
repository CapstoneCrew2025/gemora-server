package com.gemora_server.repo;

import com.gemora_server.entity.ChatMessage;
import com.gemora_server.enums.ChatMessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepo extends JpaRepository<ChatMessage, Long> {

    @Query("""
    SELECT m FROM ChatMessage m
    WHERE m.roomId = :roomId
      AND (
           (m.senderId = :userId AND m.deletedBySender = false)
        OR (m.receiverId = :userId AND m.deletedByReceiver = false)
      )
    ORDER BY m.sentAt ASC
""")
    List<ChatMessage> findChatHistoryForUser(
            @Param("roomId") String roomId,
            @Param("userId") Long userId
    );


    @Query("""
        SELECT m.roomId AS roomId, MAX(m.sentAt) AS lastAt
        FROM ChatMessage m
        WHERE m.senderId = :userId OR m.receiverId = :userId
        GROUP BY m.roomId
        ORDER BY MAX(m.sentAt) DESC
    """)

    List<Object[]> findRoomIdAndLastAtByUser(@Param("userId") Long userId);


    @Modifying
    @Query("""
        UPDATE ChatMessage m
        SET m.status = :status
        WHERE m.roomId = :roomId
          AND m.receiverId = :receiverId
          AND m.status = :currentStatus
    """)
    int markMessagesAsRead(
            @Param("roomId") String roomId,
            @Param("receiverId") Long receiverId,
            @Param("currentStatus") ChatMessageStatus currentStatus,
            @Param("status") ChatMessageStatus status
    );

    @Modifying
    @Query("""
    UPDATE ChatMessage m
    SET m.deletedBySender = true
    WHERE m.roomId = :roomId
      AND m.senderId = :userId
""")
    int deleteForSender(
            @Param("roomId") String roomId,
            @Param("userId") Long userId
    );

    @Modifying
    @Query("""
    UPDATE ChatMessage m
    SET m.deletedByReceiver = true
    WHERE m.roomId = :roomId
      AND m.receiverId = :userId
""")
    int deleteForReceiver(
            @Param("roomId") String roomId,
            @Param("userId") Long userId
    );

    @Query("""
    SELECT m FROM ChatMessage m
    WHERE m.roomId = :roomId
      AND (
           (m.senderId = :userId AND m.deletedBySender = false)
        OR (m.receiverId = :userId AND m.deletedByReceiver = false)
      )
    ORDER BY m.sentAt DESC
""")
    List<ChatMessage> findLastVisibleMessage(
            @Param("roomId") String roomId,
            @Param("userId") Long userId
    );


    @Query("""
    SELECT COUNT(m) FROM ChatMessage m
    WHERE m.roomId = :roomId
      AND m.receiverId = :userId
      AND m.status = :status
      AND m.deletedByReceiver = false
""")
    long countUnreadForUser(
            @Param("roomId") String roomId,
            @Param("userId") Long userId,
            @Param("status") ChatMessageStatus status
    );




}
