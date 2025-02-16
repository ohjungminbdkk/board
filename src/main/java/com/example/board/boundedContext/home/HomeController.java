package com.example.board.boundedContext.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
	@GetMapping("/home/main")
	@ResponseBody
	public String showHome() {
		return "hello";
	}
	
	/*
	 * @GetMapping("/") public String root() { return "redirect:/question/list"; //
	 * 서버는 http 302 응답 반환 }
	 */
    
    @GetMapping({"/", "/question/**"})
    public String forward() {
        return "forward:/index.html";  // Vue의 index.html을 반환
    }
}
