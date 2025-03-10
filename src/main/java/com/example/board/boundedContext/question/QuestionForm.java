package com.example.board.boundedContext.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {
	@NotBlank(message="제목은 필수 항목입니다.")
	@Size(max=200, message="제목을 200자 이하로 입력해주세요.")
	String subject;
	
	@NotBlank(message="내용은 필수 항목입니다.")
	@Size(max=20000, message="내용을 20,000자 이하로 입력해주세요.")
	String content;

}
