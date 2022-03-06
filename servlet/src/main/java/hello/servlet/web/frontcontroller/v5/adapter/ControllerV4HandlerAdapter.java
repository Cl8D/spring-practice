package hello.servlet.web.frontcontroller.v5.adapter;

import hello.servlet.web.frontcontroller.v2.ModelView;
import hello.servlet.web.frontcontroller.v4.ControllerV4;
import hello.servlet.web.frontcontroller.v5.MyHandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ControllerV4HandlerAdapter implements MyHandlerAdapter {
    // 핸들러가 controllerV4인지 확인
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof ControllerV4);
    }

    @Override
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        ControllerV4 controller = (ControllerV4) handler;
        Map<String, String> paramMap = createParamMap(request);

        //v4에서는 model이 필요했었다.
        Map<String, Object> model = new HashMap<>();

        // v4에서 각각의 컨트롤러들은 뷰의 논리 이름을 반환했었다. (v3의 경우 mv 객체)
        String viewName = controller.process(paramMap, model);

        // 어댑터의 경우 뷰의 이름이 아니라 modelView를 만들어서 반환해야 하기 때문에,
        // modelView로 만들어서 형식을 맞추어준다.
        ModelView mv = new ModelView(viewName);
        // model도 세팅을 해준다.
        mv.setModel(model);

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
