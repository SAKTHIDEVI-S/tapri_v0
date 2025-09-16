-- Create group_message_reads table
CREATE TABLE `group_message_reads` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    read_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (message_id) REFERENCES `group_messages`(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES `users`(id) ON DELETE CASCADE,
    UNIQUE KEY unique_message_user (message_id, user_id)
);

-- Create index for better performance
CREATE INDEX idx_group_message_reads_user_group ON `group_message_reads`(user_id, message_id);