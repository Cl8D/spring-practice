package hello.exception.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.exception.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            if (ex instanceof UserException) {
                log.info("UserException resolver to 400");
                String acceptHeader = request.getHeader("accept");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                // http 헤더의 Accept 값이 application/json이면 JSON으로 오류 내려주기
                //그게 아니라면 error/500에 정의된 오류 페이지 보여주기
                if("application/json".equals(acceptHeader)) {
                    Map<String, Object> errorResult = new HashMap<>();
                    // 예외 클래스 정보와 에러 메시지 넘겨주기
                    errorResult.put("ex", ex.getClass());
                    errorResult.put("message", ex.getMessage());

                    // json을 string 형태로 바꿔주기
                    String result = objectMapper.writeValueAsString(errorResult);

                    // errorResult를 response에 담아주기
                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    // response에 들어가게 된다
                    response.getWriter().write(result);

                    // 빈객체를 반환된다.
                    // 예외는 먹어버리지만, 정상 리턴이기 때문에 서블릿 컨테이너까지 결과가 전달된다.
                    // 여기서 딱 정상 종료가 된다.
                    return new ModelAndView();
                } else {
                    return new ModelAndView("error/500");
                }
            }
        } catch (IOException e) {
            log.error("resolver ex", e);
        }

        return null;

        /**
         * {
         *     "ex": "hello.exception.exception.UserException",
         *     "message": "사용자 오류"
         * }
         */
    }
}
