package com.example.board.boundedContext.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
public class QuestionApiController {

	private QuestionService questionService;

	@Autowired
	public QuestionApiController(QuestionService questionService) {
		this.questionService = questionService;
	}

	@GetMapping
	public Page<Question> getAllQuestion(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "kw", defaultValue = "") String kw) {
		return questionService.findQuestionAll(page, kw);
	}
}
