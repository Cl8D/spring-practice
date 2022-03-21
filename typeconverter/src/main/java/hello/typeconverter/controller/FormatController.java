package hello.typeconverter.controller;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
public class FormatController {

    @GetMapping("/formatter/edit")
    public String formatterForm(Model model) {
        Form form = new Form();
        // 숫자 10000과 현재 시간을 넣었음
        form.setNumber(10000);
        form.setLocalDateTime(LocalDateTime.now());

        model.addAttribute("form", form);
        // 둘 다 th-field로 출력했기 때문에 포맷터가 적용이 된다.
        return "formatter-form";
    }

    @PostMapping("/formatter/edit")
    public String formatterEdit(@ModelAttribute Form form) {
        // 반대로 post를 해주면 객체 형태로 담을 수 있도록...
        // 문자를 form 객체로 만들어주는 것.
        // format 정보를 통해서 하는 것임 (어노테이션에 적용한 patterns)
        return "formatter-view";
    }

    @Data
    static class Form {
        // 스프링이 제공하는 기본 컨버터
        // 이런 식으로 원하는 형태의 포맷을 지정해줄 수 있다.
        @NumberFormat(pattern = "###,###")
        private Integer number;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime localDateTime;
    }
}
