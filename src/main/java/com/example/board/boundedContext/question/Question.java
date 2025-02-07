package com.example.board.boundedContext.question;

import java.time.LocalDateTime;

import com.example.board.boundedContext.user.SiteUser;

import lombok.Data;

@Data
public class Question {
    private Integer id; // 게시물ID
    private Integer parentId; // 부모ID
    private Integer depth; // 0=질문, 1=댓글, 2=대댓글
    private String subject; // 제목
    private String content; // 내용
    private SiteUser author; // 유저정보
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}