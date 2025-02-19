package com.example.board.boundedContext.question;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.board.boundedContext.DataNotFoundException;
import com.example.board.boundedContext.answer.AnswerDto;
import com.example.board.boundedContext.user.SiteUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {

	private final QuestionMapper questionMapper;

	// 모든 질문 조회
	public Page<Question> findQuestionAll(int page, String kw) {
	    int limit = 10;
	    int offset = page * limit;
	    List<Question> questionList = questionMapper.findAllWithKw(offset, limit, kw);

	    int total = questionMapper.countAllWithKw(kw); // 전체 개수 조회
	    Pageable pageable = PageRequest.of(page, limit);
	    return new PageImpl<>(questionList, pageable, total);
	}

	// 특정 질문 조회
	public Question getQuestion(Integer id) {
		Question question = questionMapper.findById(id);
		if (question != null) {
			return question;
		} else {
			throw new DataNotFoundException("question not found");
		}
	}

	// 특정 질문의 댓글 조회
	public List<AnswerDto> getAnswersWithReplies(Integer parentId) {
		List<Question> comments = questionMapper.findByParentIdAndDepth(parentId, 1);
		List<AnswerDto> answerDtos = new ArrayList<>();

		for (Question comment : comments) {
			AnswerDto dto = new AnswerDto();
			dto.setComment(comment);
			answerDtos.add(dto);
		}
		return answerDtos;
	}

	// 질문 생성 (댓글 및 대댓글 포함)
	@Transactional
	public Question create(String subject, String content, SiteUser author, Integer parentId) {
	    Question question = new Question();
	    question.setSubject(subject);
	    question.setContent(content);
	    question.setAuthor(author);
	    question.setCreateDate(LocalDateTime.now());
	    question.setUpdateDate(question.getCreateDate());
	    question.setDepth(0); // 기본값: 질문

	    if (parentId != null) {
	        Question parentQuestion = questionMapper.findById(parentId);
	        if (parentQuestion == null) {
	            throw new IllegalArgumentException("Parent question not found");
	        }
	        question.setParentId(parentQuestion.getId());
	        question.setDepth(parentQuestion.getDepth() + 1);
	    }

	    questionMapper.insertQuestion(question);
	    return question;
	}

	// 질문 수정
	public Question modify(Integer id, String subject, String content) {
	    Question question = questionMapper.findById(id);
	    if (question == null) {
	        throw new DataNotFoundException("question not found");
	    }
	    question.setSubject(subject);
	    question.setContent(content);
	    question.setUpdateDate(LocalDateTime.now());

	    questionMapper.updateQuestion(question);
	    return question; // 수정된 객체 반환
	}

	// 질문 삭제 (하위 댓글 및 대댓글도 삭제)
	@Transactional
	public void delete(Question question) {
		List<Question> childQuestions = questionMapper.findByParentIdAndDepth(question.getId(), 1);
		for (Question child : childQuestions) {
			questionMapper.deleteQuestion(child.getId());
		}
		questionMapper.deleteQuestion(question.getId());
	}
	
	// 댓글 가져오기
	public Question getOneAnswer(Integer id, Integer depth) {
		return questionMapper.findFirstByIdAndDepth(id, depth);
	}
	
	// 댓글 수정
	public void comentModify(Integer id, String content) {
		Question question = questionMapper.findById(id);
		if (question == null) {
			throw new DataNotFoundException("answer not found");
		}
		question.setContent(content);
		questionMapper.updateAnswer(question);
	}

}
