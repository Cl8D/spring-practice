package hello.servlet.web.springmvc.v3;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

// 기존의 코드는 계속 modelAndView 객체를 생성해줬어야 했어서 좀 불편했ㄷ.
// 이를 개선해보자. -v3 같은 방식을 실무에서 가장 많이 사용한다!
@Controller
@RequestMapping("/springmvc/v3/members")
public class SpringMemberControllerV3 {
    private MemberRepository memberRepository = MemberRepository.getInstance();

    // 원래 기존의 @RequestMapping의 경우 method=""를 통해서 http Method를 입력받을 수 있는데,
    // 이를 하나의 annotation으로 만든 getmapping과 postmapping으로 제공한다.
    // (사실 requestMapping으로 해도 나오기는 하지만, 조금 더 명확하게 하기 위해서 사용한다고 생각하자!)
    @GetMapping("/new-form")
    public String newForm(){
        // 뷰의 논리 이름을 직접 반환하기 (modelAndView 객체 x)
        // 이렇게 해도 알아서 뷰의 이름이라고 생각한다.
        return "new-form";
    }

    @PostMapping("/save")
    public String save(
            // http 요청 파라미터 정보를 @RequestParam으로 전달한다.
            @RequestParam("username") String username,
            @RequestParam("age") int age,
            // 기존과 다르게 아예 model을 파라미터로 받는다.
            Model model) {

        Member member = new Member(username, age);
        memberRepository.save(member);

        model.addAttribute("member", member);
        return "save-result";
    }

    @GetMapping
    public String members(Model model) {
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);
        return "members";
    }
}
