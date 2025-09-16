package com.tapri.repository;

import com.tapri.entity.Post;
import com.tapri.entity.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    
    List<PostComment> findByPostAndIsActiveTrueOrderByCreatedAtAsc(Post post);
    
    @Query("SELECT c FROM PostComment c WHERE c.post = :post AND c.isActive = true ORDER BY c.createdAt ASC")
    List<PostComment> findActiveCommentsByPost(@Param("post") Post post);
    
    @Query("SELECT c FROM PostComment c WHERE c.post = :post AND c.isActive = true ORDER BY c.createdAt DESC")
    Page<PostComment> findActiveCommentsByPostOrderByCreatedAtDesc(@Param("post") Post post, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM PostComment c WHERE c.post = :post AND c.isActive = true")
    Long countActiveCommentsByPost(@Param("post") Post post);
    
    @Query("SELECT COUNT(c) FROM PostComment c WHERE c.post = :post")
    Long countCommentsByPost(@Param("post") Post post);
}
