package com.pfa.elearning.repository;

import com.pfa.elearning.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

       // Get conversation between two specific users
       @Query("SELECT m FROM Message m WHERE (m.sender.id = :user1Id AND m.receiver.id = :user2Id) " +
                     "OR (m.sender.id = :user2Id AND m.receiver.id = :user1Id) ORDER BY m.sentAt ASC")
       List<Message> findConversation(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

       // Get unread count for a receiver
       long countByReceiverIdAndIsReadFalse(Long receiverId);

       // Find all users who have exchanged messages with this user
       @Query("SELECT DISTINCT CASE WHEN m.sender.id = :userId THEN m.receiver.id ELSE m.sender.id END " +
              "FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
       List<Long> findDistinctContactsForUser(@Param("userId") Long userId);
}
