package com.pfa.elearning.service;

import com.pfa.elearning.model.Message;
import com.pfa.elearning.model.User;
import com.pfa.elearning.repository.MessageRepository;
import com.pfa.elearning.repository.UserRepository;
import com.pfa.elearning.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public Message sendMessage(Long senderId, Long receiverId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }
        
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", receiverId));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content.trim())
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();

        return messageRepository.save(message);
    }

    @Transactional
    public List<Message> getConversation(Long userId1, Long userId2) {
        List<Message> conversation = messageRepository.findConversation(userId1, userId2);
        
        // Mark as read for the user receiving them
        boolean markedAny = false;
        for (Message msg : conversation) {
            if (msg.getReceiver().getId().equals(userId1) && !msg.isRead()) {
                msg.setRead(true);
                markedAny = true;
            }
        }
        
        if (markedAny) {
            messageRepository.saveAll(conversation);
        }
        
        return conversation;
    }

    public List<Map<String, Object>> getConversationsPreview(Long userId) {
        List<Long> contactIds = messageRepository.findDistinctContactsForUser(userId);
        List<Map<String, Object>> previews = new ArrayList<>();
        
        for (Long contactId : contactIds) {
            User contact = userRepository.findById(contactId).orElse(null);
            if (contact != null) {
                List<Message> convo = messageRepository.findConversation(userId, contactId);
                if (!convo.isEmpty()) {
                    Message lastMsg = convo.get(convo.size() - 1);
                    
                    // Count unread messages from this contact
                    long unreadCount = convo.stream()
                            .filter(m -> m.getReceiver().getId().equals(userId) && !m.isRead())
                            .count();
                    
                    Map<String, Object> preview = new HashMap<>();
                    preview.put("contactId", contact.getId());
                    preview.put("contactName", contact.getFullName());
                    preview.put("contactEmail", contact.getEmail());
                    preview.put("contactRole", contact.getRole());
                    preview.put("lastMessage", lastMsg.getContent());
                    preview.put("lastMessageAt", lastMsg.getSentAt());
                    preview.put("unreadCount", unreadCount);
                    
                    previews.add(preview);
                }
            }
        }
        
        // Sort by most recent message
        previews.sort((p1, p2) -> {
            LocalDateTime t1 = (LocalDateTime) p1.get("lastMessageAt");
            LocalDateTime t2 = (LocalDateTime) p2.get("lastMessageAt");
            return t2.compareTo(t1);
        });
        
        return previews;
    }
}
