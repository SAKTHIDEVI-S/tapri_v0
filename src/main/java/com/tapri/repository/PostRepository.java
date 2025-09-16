package com.tapri.repository;

import com.tapri.entity.Post;
import com.tapri.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    List<Post> findByUserAndIsActiveTrueOrderByCreatedAtDesc(User user);
    
    Page<Post> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.isActive = true ORDER BY p.createdAt DESC")
    List<Post> findAllActivePostsOrderByCreatedAtDesc();
    
    @Query("SELECT p FROM Post p WHERE p.user = :user AND p.isActive = true ORDER BY p.createdAt DESC")
    List<Post> findActivePostsByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(p) FROM Post p WHERE p.user = :user AND p.isActive = true")
    Long countActivePostsByUser(@Param("user") User user);
    
    @Query("SELECT p FROM Post p WHERE p.audience = :audience AND p.isActive = true ORDER BY p.createdAt DESC")
    List<Post> findActivePostsByAudienceOrderByCreatedAtDesc(@Param("audience") String audience);
}
