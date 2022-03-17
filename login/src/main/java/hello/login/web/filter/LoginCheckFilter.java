package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class LoginCheckFilter implements Filter {
    // 홈, 회원가입, 로그인 화면, css 같은 리소스는 인증 필터 없이 모두 접근할 수 있도록
    private static final String[] whitelist
            = {"/", "/members/add", "/login", "/logout", "/css/*"};
            
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        try {
            log.info("인증 체크 필터 시작 {}", requestURI);

            // 만약 화이트 리스트가 아니라면
            if (isLoginCheckPath(requestURI)) {
                log.info("인증 체크 로직 실행 {}", requestURI);
                HttpSession session = httpRequest.getSession(false);

                // 세션이 없거나 로그인한 사용자가 아니라면 (미인증 사용자라면)
                if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    log.info("미인증 사용자 요청 {}", requestURI);
                    // 로그인 화면으로 redirect
                    // 이때, 로그인 이후에 다시 홈으로 이동하지 않고, 기존에 있던 화면으로 가도록 하기 위해
                    // 현재 요청한 경로인 requestURI를 /login의 쿼리파라미터로 함께 전달해준다.
                    // 물론, 로그인 성공했을 때 이 파라미터를 이용해서 경로로 이동하는 건 추가로 개발해야 함!
                    httpResponse.sendRedirect("/login?redirectURL="+ requestURI);
                    // 미인증 사용자는 다음으로 진행되지 않고 끝남
                    // 즉, 필터, 서블릿, 컨트롤러가 더 이상 호출되지 않음을 의미한다.
                    return;
                }
            }

            chain.doFilter(request, response);
            
        } catch (Exception e) {
            throw e; // 톰캣-WAS까지 예외를 넘겨줘야 함.
            
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
    }

    // whitelist를 제외한 나머지 모든 경로에 인증 체크 로직 동작하도록
    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }

}
