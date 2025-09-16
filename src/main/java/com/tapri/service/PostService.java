package com.tapri.service;

import com.tapri.dto.PostCommentDto;
import com.tapri.dto.PostDto;
import com.tapri.dto.PostFeedDto;
import com.tapri.dto.CreatePostRequest;
import com.tapri.entity.*;
import com.tapri.entity.SavedPost;
import com.tapri.repository.*;
import com.tapri.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private PostLikeRepository postLikeRepository;
    
    @Autowired
    private PostCommentRepository postCommentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SavedPostRepository savedPostRepository;
    
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    
    @Autowired
    private GroupMessageRepository groupMessageRepository;
    
    // Get all posts with pagination
    public Page<PostDto> getAllPosts(Pageable pageable, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Page<Post> posts = postRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
        
        return posts.map(post -> {
            boolean isLiked = postLikeRepository.existsByPostAndUser(post, currentUser);
            boolean isSaved = savedPostRepository.existsByUserAndPost(currentUser, post);
            return new PostDto(post, isLiked, isSaved);
        });
    }
    
    // Get all posts without pagination
    public List<PostDto> getAllPosts(Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Post> posts = postRepository.findAllActivePostsOrderByCreatedAtDesc();
        
        return posts.stream().map(post -> {
            boolean isLiked = postLikeRepository.existsByPostAndUser(post, currentUser);
            boolean isSaved = savedPostRepository.existsByUserAndPost(currentUser, post);
            return new PostDto(post, isLiked, isSaved);
        }).collect(Collectors.toList());
    }
    
    // Get posts for feed (frontend format) - show all posts for now
    public List<PostFeedDto> getPostsForFeed(Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get all active posts for now (we'll implement audience filtering later)
        List<Post> posts = postRepository.findAllActivePostsOrderByCreatedAtDesc();
        
        return posts.stream().map(post -> {
            boolean isLiked = postLikeRepository.existsByPostAndUser(post, currentUser);
            boolean isSaved = savedPostRepository.existsByUserAndPost(currentUser, post);
            
            // Get user avatar or default
            String userAvatar = post.getUser().getProfilePhotoUrl();
            if (userAvatar == null || userAvatar.isEmpty()) {
                userAvatar = "/uploads/profiles/default-avatar.png";
            }
            
            return new PostFeedDto(
                post.getId(),
                post.getUser().getName(),
                userAvatar,
                TimeUtils.getRelativeTime(post.getCreatedAt()),
                post.getText(),
                post.getMediaUrl(),
                post.getMediaType(),
                post.getPostType(),
                post.getLikesCount(),
                post.getCommentsCount(),
                post.getShareCount(),
                isLiked,
                isSaved
            );
        }).collect(Collectors.toList());
    }
    
    // Get post by ID with full details
    public PostDto getPostById(Long postId, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        if (!post.getIsActive()) {
            throw new RuntimeException("Post not found");
        }
        
        boolean isLiked = postLikeRepository.existsByPostAndUser(post, currentUser);
        PostDto postDto = new PostDto(post, isLiked);
        
        // Load comments
        List<PostComment> comments = postCommentRepository.findActiveCommentsByPost(post);
        List<PostCommentDto> commentDtos = comments.stream()
                .map(PostCommentDto::new)
                .collect(Collectors.toList());
        postDto.setComments(commentDtos);
        
        return postDto;
    }
    
    // Create new post
    public PostDto createPost(Long userId, String text, String mediaUrl, MediaType mediaType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Post post = new Post(user, text, mediaUrl, mediaType);
        post = postRepository.save(post);
        
        return new PostDto(post, false);
    }
    
    // Create new post with full details
    public PostDto createPost(Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Post post = new Post();
        post.setUser(user);
        post.setText(request.getText());
        post.setMediaUrl(request.getMediaUrl());
        
        // Convert String mediaType to MediaType enum
        MediaType mediaType = MediaType.IMAGE; // default
        if (request.getMediaType() != null && !request.getMediaType().isEmpty()) {
            try {
                mediaType = MediaType.valueOf(request.getMediaType().toUpperCase());
            } catch (IllegalArgumentException e) {
                // If invalid media type, default to IMAGE
                mediaType = MediaType.IMAGE;
            }
        }
        post.setMediaType(mediaType);
        
        post.setPostType(request.getPostType() != null ? request.getPostType() : "GENERAL");
        post.setAudience(request.getAudience() != null ? request.getAudience() : "EVERYONE");
        post.setIsActive(true);
        
        Post savedPost = postRepository.save(post);
        return new PostDto(savedPost, false);
    }
    
    // Like/Unlike post
    public PostDto toggleLike(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        if (!post.getIsActive()) {
            throw new RuntimeException("Post not found");
        }
        
        Optional<PostLike> existingLike = postLikeRepository.findByPostAndUser(post, user);
        
        if (existingLike.isPresent()) {
            // Unlike
            postLikeRepository.delete(existingLike.get());
        } else {
            // Like
            PostLike like = new PostLike(post, user);
            postLikeRepository.save(like);
        }
        
        // Refresh the post to get updated counts
        post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        boolean isLiked = postLikeRepository.existsByPostAndUser(post, user);
        
        // Create PostDto with updated counts
        boolean isSaved = savedPostRepository.existsByUserAndPost(user, post);
        PostDto postDto = new PostDto(post, isLiked, isSaved);
        postDto.setLikesCount(postLikeRepository.countLikesByPost(post).intValue());
        postDto.setCommentsCount(postCommentRepository.countCommentsByPost(post).intValue());
        postDto.setShareCount(post.getShareCount() != null ? post.getShareCount() : 0);
        
        return postDto;
    }
    
    // Add comment to post
    public PostCommentDto addComment(Long postId, Long userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        if (!post.getIsActive()) {
            throw new RuntimeException("Post not found");
        }
        
        PostComment comment = new PostComment(post, user, content);
        comment = postCommentRepository.save(comment);
        
        return new PostCommentDto(comment);
    }
    
    // Get comments for a post with pagination
    public Map<String, Object> getComments(Long postId, int page, int size, Long currentUserId) {
        // Verify user exists
        userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        if (!post.getIsActive()) {
            throw new RuntimeException("Post not found");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PostComment> commentsPage = postCommentRepository.findActiveCommentsByPostOrderByCreatedAtDesc(post, pageable);
        
        List<PostCommentDto> comments = commentsPage.getContent().stream()
                .map(PostCommentDto::new)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("comments", comments);
        response.put("totalElements", commentsPage.getTotalElements());
        response.put("totalPages", commentsPage.getTotalPages());
        response.put("currentPage", page);
        response.put("size", size);
        response.put("hasNext", commentsPage.hasNext());
        
        return response;
    }
    
    // Delete post (soft delete)
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this post");
        }
        
        post.setIsActive(false);
        postRepository.save(post);
    }
    
    // Delete comment (soft delete)
    public void deleteComment(Long commentId, Long userId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this comment");
        }
        
        comment.setIsActive(false);
        postCommentRepository.save(comment);
    }
    
    // Toggle save/unsave post
    public boolean toggleSavePost(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        Optional<SavedPost> existingSave = savedPostRepository.findByUserAndPost(user, post);
        
        if (existingSave.isPresent()) {
            savedPostRepository.delete(existingSave.get());
            return false; // Post unsaved
        } else {
            SavedPost savedPost = new SavedPost(user, post);
            savedPostRepository.save(savedPost);
            return true; // Post saved
        }
    }
    
    // Share post - just increment count (like other social media)
    public int sharePost(Long postId, Long userId) {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // Always increment share count (like other social media platforms)
        int newShareCount = post.getShareCount() + 1;
        post.setShareCount(newShareCount);
        postRepository.save(post);
        
        return newShareCount;
    }
    
    // Get saved posts for user
    public List<PostDto> getSavedPosts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<SavedPost> savedPosts = savedPostRepository.findByUserOrderByCreatedAtDesc(user);
        
        return savedPosts.stream().map(savedPost -> {
            Post post = savedPost.getPost();
            boolean isLiked = postLikeRepository.existsByPostAndUser(post, user);
            return new PostDto(post, isLiked, true); // isSaved is always true for saved posts
        }).collect(Collectors.toList());
    }
    
    // Get posts by user
    public List<PostDto> getPostsByUser(Long userId, Long currentUserId) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
        
        List<Post> posts = postRepository.findByUserAndIsActiveTrueOrderByCreatedAtDesc(targetUser);
        
        return posts.stream().map(post -> {
            boolean isLiked = postLikeRepository.existsByPostAndUser(post, currentUser);
            boolean isSaved = savedPostRepository.existsByUserAndPost(currentUser, post);
            return new PostDto(post, isLiked, isSaved);
        }).collect(Collectors.toList());
    }
    
    // Share post to group
    public boolean sharePostToGroup(Long postId, Long groupId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is a member of the group
        boolean isMember = groupMemberRepository.existsByGroupAndUser(group, user);
        if (!isMember) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        // Create a group message with the post content
        GroupMessage groupMessage = new GroupMessage();
        groupMessage.setGroup(group);
        groupMessage.setUser(user);
        groupMessage.setContent("Shared a post: " + post.getText());
        groupMessage.setMediaUrl(post.getMediaUrl());
        groupMessage.setMediaType(post.getMediaType());
        groupMessage.setIsActive(true);
        
        groupMessageRepository.save(groupMessage);
        
        // Increment share count for the post
        post.setShareCount(post.getShareCount() + 1);
        postRepository.save(post);
        
        return true;
    }
}
