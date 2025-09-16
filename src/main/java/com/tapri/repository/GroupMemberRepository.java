package com.tapri.repository;

import com.tapri.entity.Group;
import com.tapri.entity.GroupMember;
import com.tapri.entity.MemberRole;
import com.tapri.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    
    Optional<GroupMember> findByGroupAndUser(Group group, User user);
    
    List<GroupMember> findByGroup(Group group);
    
    List<GroupMember> findByUser(User user);
    
    @Query("SELECT m FROM GroupMember m WHERE m.group = :group ORDER BY m.role DESC, m.joinedAt ASC")
    List<GroupMember> findMembersByGroupOrdered(@Param("group") Group group);
    
    @Query("SELECT m FROM GroupMember m WHERE m.group = :group AND m.role = :role")
    List<GroupMember> findMembersByGroupAndRole(@Param("group") Group group, @Param("role") MemberRole role);
    
    @Query("SELECT COUNT(m) FROM GroupMember m WHERE m.group = :group")
    Long countMembersByGroup(@Param("group") Group group);
    
    boolean existsByGroupAndUser(Group group, User user);
}
