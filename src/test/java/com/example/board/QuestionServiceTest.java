package com.example.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;

import com.example.board.boundedContext.DataNotFoundException;
import com.example.board.boundedContext.answer.AnswerDto;
import com.example.board.boundedContext.question.Question;
import com.example.board.boundedContext.question.QuestionMapper;
import com.example.board.boundedContext.question.QuestionService;
import com.example.board.boundedContext.user.SiteUser;

class QuestionServiceTest {

	@Mock
	private QuestionMapper questionMapper;

	@InjectMocks
	private QuestionService questionService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	// 1. 질문 생성 테스트
	@Test
	void createQuestionTest() {
		SiteUser mockUser = new SiteUser();
		mockUser.setUsername("tester");

		doAnswer(invocation -> {
			Question q = invocation.getArgument(0);
			q.setId(1); // 가짜 ID 할당
			return null;
		}).when(questionMapper).insertQuestion(any(Question.class));

		Question result = questionService.create("테스트 제목", "테스트 내용", mockUser, null);

		assertThat(result.getId()).isEqualTo(1);
		assertThat(result.getSubject()).isEqualTo("테스트 제목");
		assertThat(result.getAuthor().getUsername()).isEqualTo("tester");
		verify(questionMapper, times(1)).insertQuestion(any(Question.class));
	}

	// 2. 질문 조회 테스트
	@Test
	void getQuestionTest() {
		Question mockQuestion = new Question();
		mockQuestion.setId(1);
		mockQuestion.setSubject("조회 테스트");

		when(questionMapper.findById(1)).thenReturn(mockQuestion);

		Question result = questionService.getQuestion(1);
		assertThat(result.getSubject()).isEqualTo("조회 테스트");

		assertThrows(DataNotFoundException.class, () -> questionService.getQuestion(2));
	}

	// 3. 질문 목록 조회 테스트
	@Test
	void findQuestionAllTest() {
		SiteUser user = new SiteUser();
		user.setUsername("tester");

		Question q1 = new Question();
		q1.setId(1);
		q1.setSubject("질문 1");
		q1.setAuthor(user);

		Question q2 = new Question();
		q2.setId(2);
		q2.setSubject("질문 2");
		q2.setAuthor(user);

		List<Question> mockList = Arrays.asList(q1, q2);
		when(questionMapper.findAllWithKw(0, 10, "")).thenReturn(mockList);

		Page<Question> resultPage = questionService.findQuestionAll(0, "");

		assertThat(resultPage.getContent()).hasSize(2);
		assertThat(resultPage.getContent().get(0).getSubject()).isEqualTo("질문 1");
	}

	// 4. 질문 수정 테스트
	@Test
	void modifyQuestionTest() {
		Question mockQuestion = new Question();
		mockQuestion.setId(1);
		mockQuestion.setSubject("수정 전 제목");
		mockQuestion.setContent("수정 전 내용");

		when(questionMapper.findById(1)).thenReturn(mockQuestion);

		Question result = questionService.modify(1, "수정된 제목", "수정된 내용");

		assertThat(result.getSubject()).isEqualTo("수정된 제목");
		assertThat(result.getContent()).isEqualTo("수정된 내용");
		verify(questionMapper, times(1)).updateQuestion(mockQuestion);
	}

	// 5. 질문 삭제 테스트
	@Test
	void deleteQuestionTest() {
		Question question = new Question();
		question.setId(1);

		when(questionMapper.findByParentIdAndDepth(1, 1)).thenReturn(List.of());
		doNothing().when(questionMapper).deleteQuestion(1);

		questionService.delete(question);

		verify(questionMapper, times(1)).deleteQuestion(1);
	}

	// 6. 댓글 조회 테스트
	@Test
	void getAnswersWithRepliesTest() {
		Question comment = new Question();
		comment.setId(2);
		comment.setContent("댓글 내용");

		when(questionMapper.findByParentIdAndDepth(1, 1)).thenReturn(List.of(comment));

		List<AnswerDto> answers = questionService.getAnswersWithReplies(1);

		assertThat(answers).hasSize(1);
		assertThat(answers.get(0).getComment().getContent()).isEqualTo("댓글 내용");
	}

	// 7. 댓글 수정 테스트
	@Test
	void comentModifyTest() {
		Question mockAnswer = new Question();
		mockAnswer.setId(1);
		mockAnswer.setContent("이전 댓글 내용");

		when(questionMapper.findById(1)).thenReturn(mockAnswer);

		questionService.comentModify(1, "수정된 댓글 내용");

		assertThat(mockAnswer.getContent()).isEqualTo("수정된 댓글 내용");
		verify(questionMapper, times(1)).updateAnswer(mockAnswer);
	}
}
