package com.tapri.repository;

import com.tapri.entity.Group;
import com.tapri.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
    List<Group> findByCreatedByAndIsActiveTrue(User createdBy);
    
    @Query("SELECT g FROM Group g JOIN g.members m WHERE m.user = :user AND g.isActive = true ORDER BY g.updatedAt DESC")
    List<Group> findGroupsByMember(@Param("user") User user);
    
    // Alternative query using native SQL
    @Query(value = "SELECT g.* FROM `groups` g JOIN group_members gm ON g.id = gm.group_id WHERE gm.user_id = :userId AND g.is_active = true ORDER BY g.updated_at DESC", nativeQuery = true)
    List<Group> findGroupsByMemberNative(@Param("userId") Long userId);
    
    @Query("SELECT g FROM Group g WHERE g.isActive = true AND g.name LIKE %:name%")
    List<Group> findActiveGroupsByNameContaining(@Param("name") String name);
    
    @Query("SELECT COUNT(g) FROM Group g JOIN g.members m WHERE m.user = :user AND g.isActive = true")
    Long countActiveGroupsByMember(@Param("user") User user);
    
    List<Group> findByIsActiveTrue();
}
