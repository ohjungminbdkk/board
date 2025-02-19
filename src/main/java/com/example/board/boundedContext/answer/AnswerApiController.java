package com.example.board.boundedContext.answer;

import com.example.board.boundedContext.question.Question;
import com.example.board.boundedContext.question.QuestionService;
import com.example.board.boundedContext.user.SiteUser;
import com.example.board.boundedContext.user.UserApiController;
import com.example.board.boundedContext.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnswerApiController {
	private static final Logger logger = LoggerFactory.getLogger(UserApiController.class);

	private final QuestionService questionService;
	private final UserService userService;

	// 답변 생성
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/answers/{questionId}")
	public ResponseEntity<Question> createAnswer(@PathVariable("questionId") Integer questionId,
			@Valid @RequestBody AnswerForm answerForm, Principal principal) {
		logger.info("[POST] /api/answers/" + questionId + " 답변 생성");
		Question question = questionService.getQuestion(questionId);
		SiteUser siteUser = userService.getUser(principal.getName());
		Question answer = questionService.create(null, answerForm.getContent(), siteUser, question.getId());

		return ResponseEntity.ok(answer);
	}

	// 답변 수정
	@PreAuthorize("isAuthenticated()")
	@PutMapping("/answers/{id}")
	public ResponseEntity<Question> updateAnswer(@PathVariable("id") Integer id,
			@Valid @RequestBody AnswerForm answerForm, Principal principal) {
		logger.info("[PUT] /api/answers/" + id + " 답변 수정");
		Question answer = questionService.getOneAnswer(id, 1);
		if (!answer.getAuthor().getUsername().equals(principal.getName())) {
			logger.error("[PUT] /api/answers/ 로그인 사용자 ID : " + principal.getName() + "작성자 ID : " + answer.getAuthor().getUsername(), HttpStatus.FORBIDDEN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		questionService.comentModify(answer.getId(), answerForm.getContent());
		return ResponseEntity.ok(answer);
	}

	// 답변 삭제
	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/answers/{id}")
	public ResponseEntity<Void> deleteAnswer(@PathVariable("id") Integer id, Principal principal) {
		logger.info("[DELETE] /api/answers/" + id + " 답변 삭제");
		Question answer = questionService.getOneAnswer(id, 1);
		if (!answer.getAuthor().getUsername().equals(principal.getName())) {
			logger.error("[DELETE] /api/answers/ 로그인 사용자 ID : " + principal.getName() + "작성자 ID : " + answer.getAuthor().getUsername(), HttpStatus.FORBIDDEN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		questionService.delete(answer);
		return ResponseEntity.ok().build();
	}

	// 질문의 답변 리스트 조회
	@GetMapping("/questions/{questionId}/answers")
	public ResponseEntity<List<AnswerDto>> getAnswers(@PathVariable("questionId") Integer questionId) {
		logger.info("[GET] /api/questions/" + questionId + "/answers 답변 리스트 조회");
		List<AnswerDto> answers = questionService.getAnswersWithReplies(questionId);
		return ResponseEntity.ok(answers);
	}
}
