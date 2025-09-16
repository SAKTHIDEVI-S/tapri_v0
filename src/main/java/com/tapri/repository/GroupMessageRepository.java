package com.tapri.repository;

import com.tapri.entity.Group;
import com.tapri.entity.GroupMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
    
    List<GroupMessage> findByGroupAndIsActiveTrueOrderByCreatedAtDesc(Group group);
    
    Page<GroupMessage> findByGroupAndIsActiveTrueOrderByCreatedAtDesc(Group group, Pageable pageable);
    
    @Query(value = "SELECT * FROM group_messages WHERE group_id = :groupId AND is_active = true ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    List<GroupMessage> findTop1ByGroupAndIsActiveTrueOrderByCreatedAtDesc(@Param("groupId") Long groupId);
    
    @Query("SELECT m FROM GroupMessage m WHERE m.group = :group AND m.isActive = true ORDER BY m.createdAt DESC")
    List<GroupMessage> findActiveMessagesByGroup(@Param("group") Group group);
    
    @Query("SELECT m FROM GroupMessage m WHERE m.group = :group AND m.createdAt > :since AND m.isActive = true ORDER BY m.createdAt ASC")
    List<GroupMessage> findMessagesSince(@Param("group") Group group, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(m) FROM GroupMessage m WHERE m.group = :group AND m.isActive = true")
    Long countActiveMessagesByGroup(@Param("group") Group group);
}
