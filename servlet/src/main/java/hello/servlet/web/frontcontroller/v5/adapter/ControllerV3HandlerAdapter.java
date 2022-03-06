package hello.servlet.web.frontcontroller.v5.adapter;

import hello.servlet.web.frontcontroller.v2.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;
import hello.servlet.web.frontcontroller.v5.MyHandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// controllerV3를 지원하는 어댑터
public class ControllerV3HandlerAdapter implements MyHandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        // 입력으로 들어온 handler가 ControllerV3의 인스턴스인지 물어보기
        return (handler instanceof ControllerV3);
    }

    @Override
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        // 핸들러를 ControllerV3형으로 캐스팅해준다. (어차피 support를 통해서 타입 변환이 가능한지 확인했으니까)
        ControllerV3 controller = (ControllerV3) handler;

        // controllerV3의 경우는 paramMap이 필요했었다.
        Map<String, String> paramMap = createParamMap(request);
        // controllerv3의 경우 modelview를 반환하니까 그대로 반환해준다
        ModelView mv = controller.process(paramMap);
        return mv;

    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();

        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName,
                        request.getParameter(paramName)));
        return paramMap;
    }
}
