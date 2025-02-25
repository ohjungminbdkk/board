CREATE DATABASE qna_service;

USE qna_service;

CREATE TABLE `site_user` (
  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '사용자 ID',
  `username` VARCHAR(255) NOT NULL UNIQUE COMMENT '사용자 이름',
  `email` VARCHAR(255) NOT NULL UNIQUE COMMENT '이메일',
  `password` VARCHAR(255) NOT NULL COMMENT '비밀번호',
  `role` ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER' COMMENT '역할 (ADMIN/USER)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `question` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '게시물 ID',
  `parent_id` INT UNSIGNED DEFAULT NULL COMMENT '부모 게시물 ID (Self-Join)',
  `depth` TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '0=질문, 1=답변',
  `subject` VARCHAR(200) DEFAULT NULL COMMENT '제목',
  `content` TEXT NOT NULL COMMENT '내용',
  `author_id` BIGINT(20) UNSIGNED NOT NULL COMMENT '작성자 ID (site_user 참조)',
  `create_date` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '작성된 시간',
  `update_date` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '수정된 시간',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_question_parent` FOREIGN KEY (`parent_id`) REFERENCES `question` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_question_author` FOREIGN KEY (`author_id`) REFERENCES `site_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

select * from  question;
select * from  site_user;