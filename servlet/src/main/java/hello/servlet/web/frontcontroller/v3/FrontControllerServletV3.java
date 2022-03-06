package hello.servlet.web.frontcontroller.v3;

import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v2.ModelView;
import hello.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "frontControllerServletV3", urlPatterns = "/front-controller/v3/*")
public class FrontControllerServletV3 extends HttpServlet {
    private Map<String, ControllerV3> controllerMap = new HashMap<>();

    public FrontControllerServletV3() {
        controllerMap.put("/front-controller/v3/members/new-form", new MemberFormControllerV3());
        controllerMap.put("/front-controller/v3/members/save", new MemberSaveControllerV3());
        controllerMap.put("/front-controller/v3/members", new MemberListControllerV3());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        ControllerV3 controller = controllerMap.get(requestURI);

        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // request의 파라미터 다 뽑아버리기
        Map<String, String> paramMap = createParamMap(request);
        // 이렇게 만들어진 map을 controller에게 넘기면 modelView를 반환해준다.
        ModelView mv = controller.process(paramMap);

        // 뷰의 논리 이름 꺼내기
        // ex) new-form
        String viewName = mv.getViewName();

        // 그리고 논리 이름을 실제 물리 뷰 경로로 변경해준다. + 뷰 객체 리턴
        MyView view = viewResolver(viewName);
        // 이렇게 만들어진 뷰 객체를 통해서 HTML 화면을 렌더링해준다.
        // 이때, model 정보를 함께 념겨준다. (view의 렌더링을 위해서는 필요함)
        // JSP는 request.getAttribute()로 데이터를 조회하고,
        // request.setAttribute()로 담아둔다.
        view.render(mv.getModel(), request, response);

    }

    // 실제 물리 경로로 리턴
    // ex) /WEB-INF/views/new-form.jsp로 변경됨
    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }

    // paramMap을 넘겨주자.
    // 그리고, request에 있는 모든 파라미터 이름을 다 가져온 다음에
    // (정확하게 말하면 HttpServletRequest에서 파라미터의 정보를 다 꺼내는 것)
    // 각각을 탐색하면서 paramMap에 값을 다 집어넣는다.
    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName
                        -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
