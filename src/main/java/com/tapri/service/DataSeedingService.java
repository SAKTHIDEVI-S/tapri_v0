package com.tapri.service;

import com.tapri.entity.*;
import com.tapri.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class DataSeedingService implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private PostLikeRepository postLikeRepository;
    
    @Autowired
    private PostCommentRepository postCommentRepository;
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    
    @Autowired
    private GroupMessageRepository groupMessageRepository;
    
    @Autowired
    private MessageReactionRepository messageReactionRepository;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Only seed if no users exist
        if (userRepository.count() == 0) {
            seedData();
        }
    }
    
    private void seedData() {
        // Create sample users
        List<User> users = Arrays.asList(
            new User("+919876543210", "Ramesh Kumar", "Delhi"),
            new User("+919876543211", "Suresh Singh", "Delhi"),
            new User("+919876543212", "Priya Sharma", "Delhi"),
            new User("+919876543213", "Amit Patel", "Mumbai"),
            new User("+919876543214", "Deepika Reddy", "Delhi"),
            new User("+919876543215", "Vikram Joshi", "Mumbai")
        );
        
        // Set additional user details
        users.get(0).setBio("Professional driver with 5+ years experience");
        users.get(0).setProfilePhotoUrl("https://example.com/avatars/ramesh.jpg");
        
        users.get(1).setBio("City driver, always on time!");
        users.get(1).setProfilePhotoUrl("https://example.com/avatars/suresh.jpg");
        
        users.get(2).setBio("Safe and reliable driver");
        users.get(2).setProfilePhotoUrl("https://example.com/avatars/priya.jpg");
        
        users.get(3).setBio("Experienced in long distance travel");
        users.get(3).setProfilePhotoUrl("https://example.com/avatars/amit.jpg");
        
        users.get(4).setBio("Friendly driver, great with customers");
        users.get(4).setProfilePhotoUrl("https://example.com/avatars/deepika.jpg");
        
        users.get(5).setBio("Professional and punctual");
        users.get(5).setProfilePhotoUrl("https://example.com/avatars/vikram.jpg");
        
        // Save users
        users = userRepository.saveAll(users);
        
        // Create sample posts
        List<Post> posts = Arrays.asList(
            new Post(users.get(0), "Heavy traffic on MG Road today. Consider alternate routes to avoid delays.", "https://example.com/images/traffic1.jpg", MediaType.IMAGE),
            new Post(users.get(0), "Great weather for driving today! ☀️", null, null),
            new Post(users.get(1), "Check out this cool traffic animation! 🚗", "https://example.com/images/traffic_animation.gif", MediaType.GIF),
            new Post(users.get(1), "New route discovered that saves 15 minutes!", null, null),
            new Post(users.get(2), "Safety first! Always wear your seatbelt.", null, null),
            new Post(users.get(2), "Beautiful sunset drive today 🌅", "https://example.com/images/sunset.jpg", MediaType.IMAGE),
            new Post(users.get(3), "Long drive to Mumbai completed successfully!", null, null),
            new Post(users.get(3), "Highway conditions are excellent today", null, null),
            new Post(users.get(4), "Met some amazing passengers today! 😊", null, null),
            new Post(users.get(4), "Customer service tip: Always keep water bottles in the car", null, null),
            new Post(users.get(5), "Professional driving means being on time, every time", null, null),
            new Post(users.get(5), "Weekend driving tips for new drivers", "https://example.com/videos/driving_tips.mp4", MediaType.VIDEO)
        );
        
        posts = postRepository.saveAll(posts);
        
        // Create sample likes
        List<PostLike> likes = Arrays.asList(
            new PostLike(posts.get(0), users.get(1)),
            new PostLike(posts.get(0), users.get(2)),
            new PostLike(posts.get(0), users.get(3)),
            new PostLike(posts.get(1), users.get(0)),
            new PostLike(posts.get(1), users.get(4)),
            new PostLike(posts.get(2), users.get(0)),
            new PostLike(posts.get(2), users.get(3)),
            new PostLike(posts.get(2), users.get(5)),
            new PostLike(posts.get(3), users.get(1)),
            new PostLike(posts.get(3), users.get(2)),
            new PostLike(posts.get(4), users.get(0)),
            new PostLike(posts.get(4), users.get(3)),
            new PostLike(posts.get(4), users.get(5)),
            new PostLike(posts.get(5), users.get(1)),
            new PostLike(posts.get(5), users.get(4))
        );
        
        postLikeRepository.saveAll(likes);
        
        // Create sample comments
        List<PostComment> comments = Arrays.asList(
            new PostComment(posts.get(0), users.get(1), "Thanks for the update! Will take the bypass."),
            new PostComment(posts.get(0), users.get(2), "Appreciate the heads up!"),
            new PostComment(posts.get(1), users.get(3), "Perfect weather indeed!"),
            new PostComment(posts.get(2), users.get(0), "That's really cool! How did you make it?"),
            new PostComment(posts.get(2), users.get(4), "Love the animation! 😄"),
            new PostComment(posts.get(3), users.get(2), "Which route did you take?"),
            new PostComment(posts.get(4), users.get(1), "Absolutely right! Safety first."),
            new PostComment(posts.get(5), users.get(3), "Beautiful shot! 📸"),
            new PostComment(posts.get(6), users.get(0), "Great job! How long did it take?"),
            new PostComment(posts.get(7), users.get(4), "Good to know, thanks!"),
            new PostComment(posts.get(8), users.get(2), "That's wonderful! 😊"),
            new PostComment(posts.get(9), users.get(5), "Great tip! Will implement this."),
            new PostComment(posts.get(10), users.get(1), "Very true! Punctuality is key."),
            new PostComment(posts.get(11), users.get(0), "Very helpful video! Thanks for sharing.")
        );
        
        postCommentRepository.saveAll(comments);
        
        // Create sample groups
        List<Group> groups = Arrays.asList(
            new Group("Delhi Drivers", "Professional drivers in Delhi area", users.get(0)),
            new Group("Mumbai Rides", "Mumbai city drivers community", users.get(3))
        );
        
        groups.get(0).setPhotoUrl("https://example.com/groups/delhi_drivers.jpg");
        groups.get(1).setPhotoUrl("https://example.com/groups/mumbai_rides.jpg");
        
        groups = groupRepository.saveAll(groups);
        
        // Add members to groups
        List<GroupMember> members = Arrays.asList(
            // Delhi Drivers group
            new GroupMember(groups.get(0), users.get(0), MemberRole.ADMIN),
            new GroupMember(groups.get(0), users.get(1), MemberRole.MEMBER),
            new GroupMember(groups.get(0), users.get(2), MemberRole.MEMBER),
            new GroupMember(groups.get(0), users.get(4), MemberRole.MEMBER),
            
            // Mumbai Rides group
            new GroupMember(groups.get(1), users.get(3), MemberRole.ADMIN),
            new GroupMember(groups.get(1), users.get(5), MemberRole.MEMBER),
            new GroupMember(groups.get(1), users.get(0), MemberRole.MEMBER)
        );
        
        groupMemberRepository.saveAll(members);
        
        // Create sample group messages
        List<GroupMessage> messages = Arrays.asList(
            // Delhi Drivers group messages
            new GroupMessage(groups.get(0), users.get(0), "Welcome everyone to Delhi Drivers group!"),
            new GroupMessage(groups.get(0), users.get(1), "Thanks for creating this group!"),
            new GroupMessage(groups.get(0), users.get(2), "Great to be part of this community"),
            new GroupMessage(groups.get(0), users.get(0), "Let's share traffic updates and tips here"),
            new GroupMessage(groups.get(0), users.get(4), "Will do! Thanks for the initiative"),
            new GroupMessage(groups.get(0), users.get(1), "Anyone know about the new traffic rules?"),
            new GroupMessage(groups.get(0), users.get(2), "Yes, there are some changes in Connaught Place area"),
            new GroupMessage(groups.get(0), users.get(0), "Thanks for the update!"),
            
            // Mumbai Rides group messages
            new GroupMessage(groups.get(1), users.get(3), "Welcome to Mumbai Rides group!"),
            new GroupMessage(groups.get(1), users.get(5), "Happy to be here!"),
            new GroupMessage(groups.get(1), users.get(0), "Great group for Mumbai drivers"),
            new GroupMessage(groups.get(1), users.get(3), "Let's share information about Mumbai traffic and routes"),
            new GroupMessage(groups.get(1), users.get(5), "Will definitely share useful updates"),
            new GroupMessage(groups.get(1), users.get(0), "Anyone driving to airport today?"),
            new GroupMessage(groups.get(1), users.get(3), "I'm heading there in an hour"),
            new GroupMessage(groups.get(1), users.get(5), "Traffic is heavy on Western Express Highway")
        );
        
        messages = groupMessageRepository.saveAll(messages);
        
        // Create sample message reactions
        List<MessageReaction> reactions = Arrays.asList(
            new MessageReaction(messages.get(0), users.get(1), "👍"),
            new MessageReaction(messages.get(0), users.get(2), "👏"),
            new MessageReaction(messages.get(1), users.get(0), "😊"),
            new MessageReaction(messages.get(2), users.get(1), "👍"),
            new MessageReaction(messages.get(3), users.get(2), "👍"),
            new MessageReaction(messages.get(3), users.get(4), "👏"),
            new MessageReaction(messages.get(4), users.get(0), "😊"),
            new MessageReaction(messages.get(8), users.get(5), "👍"),
            new MessageReaction(messages.get(8), users.get(0), "👏"),
            new MessageReaction(messages.get(9), users.get(3), "😊"),
            new MessageReaction(messages.get(10), users.get(5), "👍"),
            new MessageReaction(messages.get(11), users.get(0), "👏")
        );
        
        messageReactionRepository.saveAll(reactions);
        
        System.out.println("Database seeded successfully with sample data!");
    }
}
