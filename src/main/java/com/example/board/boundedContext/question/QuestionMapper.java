package com.example.board.boundedContext.question;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionMapper {

	// 모든 질문 조회 (페이징 없이 전체 조회)
	List<Question> findAll();

	// 특정 질문 조회 (ID로 찾기)
	Question findById(@Param("id") Integer id);

	// 댓글/대댓글 조회 (부모 ID & depth 기준)
	List<Question> findByParentIdAndDepth(@Param("parentId") Integer parentId, @Param("depth") Integer depth);

	// 질문 삽입
	void insertQuestion(Question question);

	// 질문 수정
	void updateQuestion(Question question);

	// 질문 삭제
	void deleteQuestion(@Param("id") Integer id);

	// 질문 목록 조회 (페이징 + 검색)
	List<Question> findAllWithKw(@Param("offset") int offset, @Param("limit") int limit, @Param("kw") String kw);

	// 답변 수정
	void updateAnswer(Question question);
	
	// 답변 조회
	Question findFirstByIdAndDepth(@Param("id") Integer id, @Param("depth") Integer depth);
	
	int countAllWithKw(@Param("kw") String kw);
}
