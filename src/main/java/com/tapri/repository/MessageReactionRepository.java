package com.tapri.repository;

import com.tapri.entity.GroupMessage;
import com.tapri.entity.MessageReaction;
import com.tapri.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {
    
    Optional<MessageReaction> findByMessageAndUser(GroupMessage message, User user);
    
    List<MessageReaction> findByMessage(GroupMessage message);
    
    @Query("SELECT r FROM MessageReaction r WHERE r.message = :message")
    List<MessageReaction> findReactionsByMessage(@Param("message") GroupMessage message);
    
    @Query("SELECT r FROM MessageReaction r WHERE r.message = :message AND r.emoji = :emoji")
    List<MessageReaction> findReactionsByMessageAndEmoji(@Param("message") GroupMessage message, @Param("emoji") String emoji);
    
    @Query("SELECT COUNT(r) FROM MessageReaction r WHERE r.message = :message")
    Long countReactionsByMessage(@Param("message") GroupMessage message);
    
    boolean existsByMessageAndUser(GroupMessage message, User user);
}
