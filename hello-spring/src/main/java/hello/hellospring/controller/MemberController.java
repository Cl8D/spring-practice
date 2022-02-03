package hello.hellospring.controller;

import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MemberController {

    // spring container에 등록해주기
    private final MemberService memberService;

    // 이때 autowired를 사용하면 spring container에 있는 memberservice와 연결이 된다
    // 즉, 스프링 컨테이너에서 가져오는 것
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }



}
