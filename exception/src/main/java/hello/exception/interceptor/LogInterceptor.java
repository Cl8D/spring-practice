package hello.exception.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
// 인터셉터는 dispatchType과 무관하게 항상 호출되지만,
// 요청 경로에 따른 추가/제외가 비교적 쉽기 때문에 excludePathPatterns를 사용하면 된다.
public class LogInterceptor implements HandlerInterceptor {
    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();
        request.setAttribute(LOG_ID, uuid);

        log.info("REQUEST [{}][{}][{}][{}]", uuid,
                request.getDispatcherType(), requestURI, handler);
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle [{}]", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String)request.getAttribute(LOG_ID);

        log.info("RESPONSE [{}][{}][{}]", logId, request.getDispatcherType(),
                requestURI);

        if (ex != null) {
            log.error("afterCompletion error!!", ex);
        }
    }

    /**
     *   REQUEST [81f66a3d-f1c3-47ab-b2e8-89fab9c23f30][REQUEST][/error-ex][hello.exception.servlet.ServletExController#errorEx()]
     *   RESPONSE [81f66a3d-f1c3-47ab-b2e8-89fab9c23f30][REQUEST][/error-ex]
     *
     * 예외가 발생했기 때문에, postHandle은 호출되지 않고
     * afterCompletion만 호출되었다. (얘는 항상 호출)
     *   afterCompletion error!!
     *
     */
}
