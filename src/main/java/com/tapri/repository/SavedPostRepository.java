package com.tapri.repository;

import com.tapri.entity.Post;
import com.tapri.entity.SavedPost;
import com.tapri.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {
    
    Optional<SavedPost> findByUserAndPost(User user, Post post);
    
    boolean existsByUserAndPost(User user, Post post);
    
    void deleteByUserAndPost(User user, Post post);
    
    List<SavedPost> findByUserOrderByCreatedAtDesc(User user);
}
