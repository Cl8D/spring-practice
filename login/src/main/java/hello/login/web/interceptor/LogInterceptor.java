package hello.login.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


@Slf4j
public class LogInterceptor implements HandlerInterceptor {
    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        // 요청 로그 구분을 위한 uuid 생성
        String uuid = UUID.randomUUID().toString();

        // 서블릿 필터는 지역변수로 해결이 되지만, 스프링 인터셉터의 호출 시점은 완전히 분리되어 있다.
        // 그래서, preHandler에서 지정한 값을 다른 함수에서 사용하기 위해서는 담아둬야 한다.
        // LogInterceptor도 싱글톤처럼 사용되니까 멤버 변수는 위험하고,
        // 그래서 request에 담아둔 것. (나중에 getAttribute로 찾으면 된다)
        request.setAttribute(LOG_ID, uuid);


        // 핸들러 매핑은 보통 @Controller나 @RequestMapping을 활용하여 사용하는데,
        // @RequestMapping에서는 핸들러 정보로 handlerMethod가 넘어온다.
        // 그러나, 만약 /resource/static 같은 정적 리소스 호출시 ResourceHttpRequestHandler가 넘어온다.

        // 정리) @RequestMapping : HandlerMethod
        // 정적 리소스: ResourceHttpRequestHandler
        if(handler instanceof HandlerMethod) {
            // 호출할 컨트롤러 메서드의 모든 정보가 포함되어 있음
            HandlerMethod hm = (HandlerMethod) handler;
        }

        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler);
        // true라면 정상 호출로, 다음 인터셉터나 컨트롤러가 호출된다.
        return true; //false 진행X
    }

    @Override
    // 예외 발생시 실행이 안 된다는 점!! 주의.
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle [{}]", modelAndView);
    }

    @Override
    /// 예외가 발생해도 호출된다.
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String) request.getAttribute(LOG_ID);
        log.info("RESPONSE [{}][{}]", logId, requestURI);
        // ex = 예외 정보
        if (ex != null) {
            log.error("afterCompletion error!!", ex);
        }
    }
}
