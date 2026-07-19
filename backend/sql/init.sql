CREATE DATABASE IF NOT EXISTS campus_lost_found DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE campus_lost_found;

-- User table
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `username` VARCHAR(50) NOT NULL COMMENT 'Username',
    `password` VARCHAR(100) NOT NULL COMMENT 'Password',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT 'Nickname',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT 'Avatar URL',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT 'Phone number',
    `email` VARCHAR(100) DEFAULT NULL COMMENT 'Email',
    `role` INT NOT NULL DEFAULT 0 COMMENT 'Role: 0-student, 1-admin',
    `status` INT NOT NULL DEFAULT 0 COMMENT 'Status: 0-normal, 1-disabled',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User table';

-- Item info table
CREATE TABLE IF NOT EXISTS `item_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `user_id` BIGINT NOT NULL COMMENT 'Publisher user ID',
    `title` VARCHAR(100) NOT NULL COMMENT 'Title',
    `description` TEXT DEFAULT NULL COMMENT 'Description',
    `images` TEXT DEFAULT NULL COMMENT 'Image URLs (comma separated)',
    `category` VARCHAR(50) DEFAULT NULL COMMENT 'Category',
    `location` VARCHAR(200) DEFAULT NULL COMMENT 'Location',
    `lost_found_time` DATETIME DEFAULT NULL COMMENT 'Lost/Found time',
    `type` INT NOT NULL DEFAULT 0 COMMENT 'Type: 0-lost, 1-found',
    `status` INT NOT NULL DEFAULT 0 COMMENT 'Status: 0-pending, 1-rejected, 2-published, 3-completed',
    `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT 'Reject reason',
    `view_count` INT DEFAULT 0 COMMENT 'View count',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Item info table';

-- Message table
CREATE TABLE IF NOT EXISTS `message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `item_id` BIGINT NOT NULL COMMENT 'Item ID',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `content` TEXT NOT NULL COMMENT 'Message content',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    PRIMARY KEY (`id`),
    KEY `idx_item_id` (`item_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Message table';

-- Claim record table
CREATE TABLE IF NOT EXISTS `claim_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `item_id` BIGINT NOT NULL COMMENT 'Item ID',
    `claim_user_id` BIGINT NOT NULL COMMENT 'Claim user ID',
    `owner_user_id` BIGINT NOT NULL COMMENT 'Item owner user ID',
    `claim_info` TEXT DEFAULT NULL COMMENT 'Claim information',
    `status` INT NOT NULL DEFAULT 0 COMMENT 'Status: 0-pending, 1-approved, 2-rejected',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    PRIMARY KEY (`id`),
    KEY `idx_item_id` (`item_id`),
    KEY `idx_claim_user_id` (`claim_user_id`),
    KEY `idx_owner_user_id` (`owner_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Claim record table';

-- Blacklist table
CREATE TABLE IF NOT EXISTS `blacklist` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT 'Blacklist reason',
    `status` INT NOT NULL DEFAULT 1 COMMENT 'Status: 0-inactive, 1-active',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Blacklist table';

-- Notice table
CREATE TABLE IF NOT EXISTS `notice` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `title` VARCHAR(200) NOT NULL COMMENT 'Title',
    `content` TEXT NOT NULL COMMENT 'Content',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Notice table';

-- Insert default admin account (password: admin123)
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', 'admin123', 'Administrator', 1, 0);

-- Insert sample notice
INSERT INTO `notice` (`title`, `content`) VALUES
('Welcome to Campus Lost and Found', 'This is a platform for students to publish and find lost items. Please use it responsibly.');
