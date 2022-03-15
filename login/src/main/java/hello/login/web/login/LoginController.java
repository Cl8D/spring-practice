package hello.login.web.login;

import hello.login.domain.login.LoginForm;
import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "login/loginForm";
    }

    /*********************************/

    //@PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm loginForm,
                        BindingResult bindingResult, HttpServletResponse response) {

        if (bindingResult.hasErrors())
            return "login/loginForm";

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        log.info("login? {}", loginMember);

        // 로그인 실패
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리

        // 서버에서 http response를 보낼 때 쿠키를 함께 보내주기 =
        // 쿠키에 시간 정보를 주지 않으면 세션 쿠키 (브라우저 종료 시 모두 종료됨)
        // 로그인 성공 시 쿠키를 생성하고 httpServletResponse에 담는다.
        // 쿠키 이름은 memberId이며, 값은 회원의 id값을 넣어준다.
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);

        return "redirect:/";
    }

    /*****************************/

    // 세션 관리 기능을 적용해보자.
    private final SessionManager sessionManager;

    //@PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute LoginForm loginForm,
                        BindingResult bindingResult, HttpServletResponse response) {

        if (bindingResult.hasErrors())
            return "login/loginForm";

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        log.info("login? {}", loginMember);

        // 로그인 실패
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리
        // 세션 관리자를 통해서 세션을 생성하고, 회원 데이터를 보관하자.
        // 이때 세션 관리자를 통해 쿠키도 함께 만들어짐.
        sessionManager.createSession(loginMember, response);

        return "redirect:/";
    }

    /*****************************/

    @PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute LoginForm loginForm,
                          BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors())
            return "login/loginForm";

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        log.info("login? {}", loginMember);

        // 로그인 실패
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리
        // 세션이 있으면 반환하고, 없으면 신규 세션을 생성해준다.
        HttpSession session = request.getSession();
        // 세션에 로그인 회원 정보를 보관해준다.
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:/";
    }


    /*****************************/

    // 로그아웃 기능 추가
    //@PostMapping("/logout")
    public String logout (HttpServletResponse response) {
        expireCookie(response, "memberId");
        return "redirect:/";
    }

    /*****************************/

    //@PostMapping("/logout")
    public String logoutV2(HttpServletRequest request) {
        sessionManager.expire(request);
        return "redirect:/";
    }

    /*****************************/
    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) {
        // 세션 삭제 (여기서는 아마 기존 세션을 반환할 것이다)
        HttpSession session = request.getSession(false);

        if(session != null)
            session.invalidate(); // 세션 제거

        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }



}
