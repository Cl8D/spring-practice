package jpabook.jpashop2;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// View 환경 설정
@Controller
public class HelloController {

    // hello라는 url이 오면 호출
    @GetMapping("hello")
    // model에 데이터를 실어서 view에게 넘기기
    // name이 data일 때 value 값으로 "hello!!!"를 넘김
    public String hello(Model model){
        model.addAttribute("data", "hello!!!");
        // resources -> templates -> hello.html과 매핑
        // 즉, 여기서 data로 "hello!!!"를 넘겨주었기 때문에, 화면 출력으로 안녕하세요, hello!!!가 나오게 된다.
        return "hello";
    }
}
