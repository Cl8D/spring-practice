package hello.hellospring.controller;

import hello.hellospring.domain.Member;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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

    @GetMapping("/members/new")
    public String createForm() {
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(MemberForm form) {
       Member member = new Member();
       member.setName(form.getName());

       // 멤버 목록에 추가
       memberService.join(member);

       //홈 화면으로 보내기
       return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model)
    {
        // 모든 멤버 조회
        List<Member> members = memberService.findMembers();
        // 멤버의 리스트를 전부 모델에 담아서 화면에 넘겨줄 것임
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
