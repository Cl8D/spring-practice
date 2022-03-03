package jpabook.jpashop2.controller;

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
        // home.html로 제어가 이동한다.
        return "home";
    }
}
