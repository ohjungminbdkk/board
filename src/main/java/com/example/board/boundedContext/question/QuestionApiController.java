package com.example.board.boundedContext.question;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.board.boundedContext.DataNotFoundException;
import com.example.board.boundedContext.user.SiteUser;
import com.example.board.boundedContext.user.UserApiController;
import com.example.board.boundedContext.user.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionApiController {
	private static final Logger logger = LoggerFactory.getLogger(UserApiController.class);

	private final QuestionService questionService;
	private final UserService userService;

	// 전체 질문 목록 조회 (페이징 지원)
	@GetMapping
	public ResponseEntity<Page<Question>> getAllQuestions(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "kw", defaultValue = "") String kw) {
		if (!kw.isEmpty()) {
			logger.info("[GET] KEYWORD [" + kw + "] 키워드 조회");
			return ResponseEntity.ok(questionService.findQuestionKw(page, kw));
		} else {
			logger.info("[GET] 질문 리스트 조회");
			return ResponseEntity.ok(questionService.findQuestionAll(page));
		}
	}

	// 질문 상세 조회
	@GetMapping("/{id}")
	public ResponseEntity<Question> getQuestionDetail(@PathVariable("id") Integer id) {
		logger.info("[GET] /api/questions/" + id + " 질문 상세 조회");
		try {
			Question question = questionService.getQuestion(id);
			return ResponseEntity.ok(question);
		} catch (DataNotFoundException e) {
			logger.warn("[GET] /api/questions/" + id + " 질문이 존재하지 않음", HttpStatus.NOT_FOUND);
			return ResponseEntity.notFound().build();
		}
	}

	// 질문 생성 (로그인 필요)
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public ResponseEntity<Question> createQuestion(@RequestBody QuestionForm questionForm, Principal principal) {
		logger.info("[POST] /api/questions/create 질문 생성");
		SiteUser siteUser = userService.getUser(principal.getName());
		Question newQuestion = questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser,
				null);
		return ResponseEntity.ok(newQuestion);
	}

	// 질문 수정 (로그인 필요, 작성자만 가능)
	@PreAuthorize("isAuthenticated()")
	@PutMapping("/{id}")
	public ResponseEntity<Question> updateQuestion(@PathVariable("id") Integer id,
			@RequestBody QuestionForm questionForm, Principal principal) {
		logger.info("[PUT] /api/questions/" + id + " 질문 수정");
		try {
			Question question = questionService.getQuestion(id);

			// 로그인한 사용자가 작성자인지 확인
			if (!question.getAuthor().getUsername().equals(principal.getName())) {
				logger.error("[PUT] /api/questions/" + id + " 로그인 사용자 ID : " + principal.getName() + "작성자 ID : "
						+ question.getAuthor().getUsername(), HttpStatus.FORBIDDEN);
				return ResponseEntity.status(403).build();
			}

			// 질문 수정 후, 수정된 객체 반환
			Question updatedQuestion = questionService.modify(id, questionForm.getSubject(), questionForm.getContent());
			return ResponseEntity.ok(updatedQuestion);
		} catch (DataNotFoundException e) {
			logger.warn("[PUT] /api/questions/" + id + " 질문이 존재하지 않음", HttpStatus.NOT_FOUND);
			return ResponseEntity.notFound().build();
		}
	}

	// 질문 삭제 (로그인 필요, 작성자만 가능)
	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteQuestion(@PathVariable("id") Integer id, Principal principal) {
		logger.info("[DELETE] /api/questions/" + id + " 질문 삭제");
		try {
			Question question = questionService.getQuestion(id);

			// 로그인한 사용자가 작성자인지 확인
			if (!question.getAuthor().getUsername().equals(principal.getName())) {
				logger.error("[DELETE] /api/questions/" + id + " 로그인 사용자 ID : " + principal.getName() + "작성자 ID : "
						+ question.getAuthor().getUsername(), HttpStatus.FORBIDDEN);
				return ResponseEntity.status(403).build();
			}

			// 질문 삭제
			questionService.delete(question);
			return ResponseEntity.noContent().build(); // 204 No Content 반환
		} catch (DataNotFoundException e) {
			logger.warn("[DELETE] /api/questions/" + id + " 질문이 존재하지 않음", HttpStatus.NOT_FOUND);
			return ResponseEntity.notFound().build();
		}
	}
}
