package com.example.board.boundedContext.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"/", "/question/**"})
    public String forward() {
        return "forward:/index.html";  // Vue의 index.html을 반환
    }
}
