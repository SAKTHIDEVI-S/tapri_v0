package com.tapri.repository;

import com.tapri.entity.Post;
import com.tapri.entity.PostLike;
import com.tapri.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    
    Optional<PostLike> findByPostAndUser(Post post, User user);
    
    List<PostLike> findByPost(Post post);
    
    @Query("SELECT l FROM PostLike l WHERE l.post = :post")
    List<PostLike> findLikesByPost(@Param("post") Post post);
    
    @Query("SELECT COUNT(l) FROM PostLike l WHERE l.post = :post")
    Long countLikesByPost(@Param("post") Post post);
    
    boolean existsByPostAndUser(Post post, User user);
}
