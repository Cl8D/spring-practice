package hello.servlet.web.frontcontroller.v1;

import hello.servlet.web.frontcontroller.v1.controller.MemberFormControllerV1;
import hello.servlet.web.frontcontroller.v1.controller.MemberListControllerV1;
import hello.servlet.web.frontcontroller.v1.controller.MemberSaveControllerV1;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// v1 하위에 어떤 게 들어와도 일단 이 서블릿이 가장 먼저 호출된다.
@WebServlet(name = "frontControllerServletV1", urlPatterns = "/front-controller/v1/*")
public class FrontControllerServletV1 extends HttpServlet {

    // 매핑 정보를 담는 맵
    // 어떠한 url이 호출되면 (string) controllerv1 중에 하나를 호출하도록
    private Map<String, ControllerV1> controllerMap = new HashMap<>();

    public FrontControllerServletV1() {
        // 1) URL 매핑 정보에서 컨트롤러 조회하기
        controllerMap.put("/front-controller/v1/members/new-form", new MemberFormControllerV1());
        controllerMap.put("/front-controller/v1/members/save", new MemberSaveControllerV1());
        controllerMap.put("/front-controller/v1/members", new MemberListControllerV1());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 이렇게 하면 uri의 정보가 (localhost:8080 뒷부분에 오는 정보)
        // 즉, map의 key값과 동일한 형태의 정보가 들어간다.
        String requestURI = request.getRequestURI();

        // uri를 통해서 controller 찾기
        // 즉, 우리가 new로 넣은 객체가 반환되는 것
        ControllerV1 controller = controllerMap.get(requestURI);

        // 만약 요청한 uri가 존재하지 않으면 404 에러
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 그게 아니라면 우리가 만든 서블릿 인터페이스 호출
        controller.process(request, response);
    }
}
