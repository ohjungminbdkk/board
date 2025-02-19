package com.example.board.boundedContext.answer;

import java.util.List;

import com.example.board.boundedContext.question.Question;

import lombok.Data;

@Data
public class AnswerDto {
	private Question comment; // 댓글 (depth=1)
}