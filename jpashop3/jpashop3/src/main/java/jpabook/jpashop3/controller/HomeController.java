package jpabook.jpashop3.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
// 로그 출력 메서드
@Slf4j
public class HomeController {

    // 가장 첫 화면 구성
    @RequestMapping("/")
    public String home() {
        log.info("home controller!");
        return "home";
    }
}
