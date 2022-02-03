package hello.hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "hello!!!");
        // 문자열을 리턴하면 hello.html로 이동하게 됨 (templates로 이동)
        return "hello";

    }

    @GetMapping("hello-mvc")
    public String helloMVC(@RequestParam("name") String name, Model model) {
        // model에 담게 되면 view에서 렌더링 시 사용하게 된다
        model.addAttribute("name", name);
        return "hello-template";
    }

    @GetMapping("hello-string")
    @ResponseBody
    // http의 body부에 return의 내용을 직접 넣어주겠다는 것
    public String helloString(@RequestParam("name") String name) {
        return "hello " + name; // ex. "hello spring"
    }


    @GetMapping("hello-api")
    @ResponseBody
    public Hello helloApi(@RequestParam("name") String name) {
        // ctrl + shift + enter -> 문장 자동 완성
        Hello hello = new Hello();
        hello.setName(name);
        return hello;
    }

    // alt + insert -> getter & setter 바로 생성 가능
    static class Hello {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
