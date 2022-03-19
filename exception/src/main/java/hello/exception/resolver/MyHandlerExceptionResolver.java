package hello.exception.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class MyHandlerExceptionResolver implements HandlerExceptionResolver {

    @Override
    // 보면 리턴값으로 modelAndView를 리턴하는데,
    // 이는 예외를 처리해서 정상 흐름처럼 동작하게 하려고 이러는 것.
    // 즉, exception을 해결하기 위한 것이라고 생각하자.
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            // IllegalArgumentException 발생 시 400 에러 지정,
            // 비어있는 modelAndView 리턴. -> 정상 리턴이니까 예외가 먹힘
            if (ex instanceof IllegalArgumentException) {
                log.info("IllegalArgumentException resolver to 400");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                return new ModelAndView();
            }
        } catch (IOException e) {
            log.error("resolver ex", e);

        }
        // null 리턴 시 그냥 발생한 예외를 서블릿 밖으로 던지는 것
        return null;

        /**
         * timestamp": "2022-03-19T02:31:32.778+00:00",
         *     "status": 400,
         *     "error": "Bad Request",
         *     "exception": "java.lang.IllegalArgumentException",
         *     "path": "/api/members/bad"
         *
         *     이런 식으로 400 에러가 찍히는 걸 확인할 수 있다.
         */
    }
}
