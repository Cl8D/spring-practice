package hello.hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 이러면 localhost:8080 접속 시 여기로 감
    @GetMapping("/")
    public String Home() {
        return "home";
    }
}
