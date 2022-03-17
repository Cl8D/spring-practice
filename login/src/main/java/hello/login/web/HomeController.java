package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.argumentresolver.Login;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;

    //@GetMapping("/")
    public String home() {
        return "home";
    }

    /******************************/

    //@GetMapping("/")
    public String homeLogin(
            // cookieValue를 통해 쿠키를 조회할 수 있다.
            // 이때, 로그인 하지 않은 사용자를 위해 required 옵션은 false로 둔다.
            @CookieValue(name="memberId", required = false) Long memberId,
            Model model) {

        // 로그인 쿠키(memberId)가 없는 사용자는 기존의 home으로 보내준다.
        if(memberId == null)
            return "home";

        // 로그인
        Member loginMember = memberRepository.findById(memberId);
        // 혹은, 로그인 쿠키가 있어도 회원이 없다면 home으로 보낸다.
        if(loginMember == null)
            return "home";

        // 로그인 쿠키가 있으면서 회원이 존재한다면 로그인 사용자 전용 홈 화면으로 보내준다.
        // 이때, 홈 화면에 회원 이름을 출력해주기 때문에 member를 함께 model에 담아 전달한다.
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    /******************************/
    private final SessionManager sessionManager;

    //@GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model) {

        // 세션 관리자에 저장된 회원 정보를 조회한다.
        Member member = (Member)sessionManager.getSession(request);

        if(member == null)
            return "home";

        // 로그인
        model.addAttribute("member", member);
        return "loginHome";
    }

    /******************************/

    //@GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model) {

        // 만약 세션이 없으면 home으로 이동
        HttpSession session = request.getSession(false);
        if(session == null)
            return "home";

        // 세션에 회원 데이터가 없다면 home으로 이동
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null)
            return "home";

        // 세션이 그대로 유지되고 있다면 로그인 화면으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    /******************************/

    //@GetMapping("/")
    public String homeLoginV3Spring(
            @SessionAttribute(name=SessionConst.LOGIN_MEMBER, required = false)
            Member loginMember, Model model) {

        // 세션에 회원 데이터가 없으면 home으로 이동
        if(loginMember == null)
            return "home";

        // 세션이 유지된다면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    /******************************/

    @GetMapping("/")
    public String homeLoginV3ArgumentResolver(
            // 직접 만든 login 어노테이션이 있으면,
            // 직접 만든 argumentResolver가 동작하여
            // 자동으로 세션에 있는 로그인 회원을 찾아주고, 세션이 없다면 null을 반환한다.
            // 걍 자동으로 로그인된 사용자라고 세션에서 찾아서 넣어주는 것임
            @Login Member loginMember, Model model) {

        // 세션에 회원 데이터가 없으면 home으로 이동
        if(loginMember == null)
            return "home";

        // 세션이 유지된다면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}