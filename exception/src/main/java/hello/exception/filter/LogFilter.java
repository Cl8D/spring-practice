package hello.exception.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
       log.info("log filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        try{
            log.info("REQUEST [{}][{}][{}]", uuid, request.getDispatcherType(), requestURI);
            chain.doFilter(request, response);
        }catch (Exception e) {
          throw e;
        } finally {
            log.info("RESPONSE [{}][{}][{}]", uuid, request.getDispatcherType(), requestURI);
        }
    }

    /**
     * 동작 흐름)
     * 처음에 웹 페이지 접속 -> logFilter 호출!
     *    filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
     *
     * 이때 request.getDispatcherType() => REQUEST 로그
     *   REQUEST [e7691c21-703f-4d24-bf02-6bd9d8bea97f][REQUEST][/error-ex]
     *
     * chain.doFilter로 인해 필터, 서블릿을 지나서
     * 컨트롤러의 ErrorEx()가 실행된다.
     * 이때 throw new로 인해 예외가 발생하고,
     * 예외가 발생했으니 다시 돌아와서 try-catch문으로 온다.
     * 여기서 catch문을 통해 예외를 잡게 되고,
     * 그리고 finally문이 동작한다 -> RESPONSE 로그
     *   RESPONSE [e7691c21-703f-4d24-bf02-6bd9d8bea97f][REQUEST][/error-ex]
     * : 이때, 아직 type이 request인 이유는, was까지 올라간 뒤 error로 리다이렉트될 때 필요한 request에서 type의 값이 error로 세팅되기 때문. 아직까지는 request.
     *
     * 아무튼, throw로 던진 예외는 WAS까지 올라가게 되고,
     *   java.lang.RuntimeException: 예외 발생!
     * WAS는 /error-page/500을 재요청하며 우리가 커스텀했던 함수에 의해 (customize 함수)
     * dispatcherType이 ERROR로 된 상태로 해당 페이지를 요청하게 된다. => REQUEST 로그
     * : 여기서 dispatcherType 덕분에 오류 페이지를 위한 요청임을 알 수 있는 것임!
     *   REQUEST [1fca64f4-e590-4d6f-96ad-f5ec432705f4][ERROR][/error-page/500]
     * 그래서 오류 펭지를 출력하고, 마지막에 다시 finally에 의해 response가 찍힌다. => RESPONSE 로그
     *   RESPONSE [1fca64f4-e590-4d6f-96ad-f5ec432705f4][ERROR][/error-page/500]
     */

    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}
