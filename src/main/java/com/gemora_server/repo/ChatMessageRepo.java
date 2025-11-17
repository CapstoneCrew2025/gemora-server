package com.gemora_server.repo;

import com.gemora_server.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepo extends JpaRepository<ChatMessage,Long> {

    List<ChatMessage> findByRoomIdOrderBySentAtAsc(String roomId);

}
