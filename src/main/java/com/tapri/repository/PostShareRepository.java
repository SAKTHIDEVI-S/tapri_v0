package com.tapri.repository;

import com.tapri.entity.Post;
import com.tapri.entity.PostShare;
import com.tapri.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostShareRepository extends JpaRepository<PostShare, Long> {
    
    Optional<PostShare> findByUserAndPost(User user, Post post);
    
    boolean existsByUserAndPost(User user, Post post);
    
    void deleteByUserAndPost(User user, Post post);
}
