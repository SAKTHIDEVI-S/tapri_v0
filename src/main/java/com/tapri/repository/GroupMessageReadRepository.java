package com.tapri.repository;

import com.tapri.entity.Group;
import com.tapri.entity.GroupMessage;
import com.tapri.entity.GroupMessageRead;
import com.tapri.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMessageReadRepository extends JpaRepository<GroupMessageRead, Long> {
    
    // Check if user has read a specific message
    boolean existsByMessageAndUser(GroupMessage message, User user);
    
    // Find read record for a specific message and user
    Optional<GroupMessageRead> findByMessageAndUser(GroupMessage message, User user);
    
    // Get all read messages for a user in a group
    @Query("SELECT gmr.message FROM GroupMessageRead gmr WHERE gmr.user = :user AND gmr.message.group = :group")
    List<GroupMessage> findReadMessagesByUserAndGroup(@Param("user") User user, @Param("group") Group group);
    
    // Count unread messages for a user in a group (excluding user's own messages)
    @Query("SELECT COUNT(gm) FROM GroupMessage gm WHERE gm.group = :group AND gm.isActive = true AND gm.user != :user AND gm NOT IN " +
           "(SELECT gmr.message FROM GroupMessageRead gmr WHERE gmr.user = :user)")
    long countUnreadMessagesByUserAndGroup(@Param("user") User user, @Param("group") Group group);
    
    // Get last read message for a user in a group
    @Query("SELECT gmr.message FROM GroupMessageRead gmr WHERE gmr.user = :user AND gmr.message.group = :group " +
           "ORDER BY gmr.readAt DESC LIMIT 1")
    Optional<GroupMessage> findLastReadMessageByUserAndGroup(@Param("user") User user, @Param("group") Group group);
}
