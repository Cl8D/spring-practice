package hello.servlet.web.frontcontroller.v5;

import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v2.ModelView;
import hello.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;
import hello.servlet.web.frontcontroller.v4.controller.MemberFormControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberListControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberSaveControllerV4;
import hello.servlet.web.frontcontroller.v5.adapter.ControllerV3HandlerAdapter;
import hello.servlet.web.frontcontroller.v5.adapter.ControllerV4HandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "frontControllerServletV5", urlPatterns = "/front-controller/v5/*")
public class FrontControllerServletV5 extends HttpServlet {
    // 기존에는 map에 특정 컨트롤러만 들어갈 수 있었지만,
    // 이제는 아무 컨트롤러나 다 들어갈 수 있어야 하기 때문에 Object 형으로 받는다.
    private final Map<String, Object> handlerMappingMap = new HashMap<>();

    // 어댑터는 여러 개 존재하기 때문에 그러한 어댑터들을 저장하는 리스트
   private final List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();

    public FrontControllerServletV5() {
        // 핸들러 매핑 초기화
        initHandlerMappingMap();
        // 어댑터 초기화
        initHandlerAdapters();
    }

    // 매핑 정보 저장. uri - 컨트롤러 객체
    private void initHandlerMappingMap() {
        // v3 관련 컨트롤러 저장
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());

        // v4 관련 컨트롤러 저장
        handlerMappingMap.put("/front-controller/v5/v4/members/new-form", new MemberFormControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members/save", new MemberSaveControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members", new MemberListControllerV4());
    }

    // 어댑터 정보 저장.
    private void initHandlerAdapters() {
        // v3 추가
        handlerAdapters.add(new ControllerV3HandlerAdapter());
        // v4 추가
        handlerAdapters.add(new ControllerV4HandlerAdapter());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 여기서 사용자가 원하는 핸들러(컨트롤러) 정보가 들어감
        Object handler = getHandler(request);

        // 핸들러가 null이면 오류 처리
        if (handler == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 해당 핸들러를 처리할 수 있는 핸들러 어댑터를 조회하자.
        MyHandlerAdapter adapter = getHandlerAdapter(handler);

        // 그렇게 찾은 어댑터에 대한 실제 컨트롤러 (핸들러)를 호출한다. -> 어댑터에 맞춰서 modelView 객체를 반환한다.
        ModelView mv = adapter.handle(request, response, handler);

        // 이후 modelView에 담긴 뷰의 논리명을 리졸버를 통해 물리 주소로 변경한 뒤 view를 리턴 받고
        // JSP로 포워딩해서 렌더링을 진행한다.
        MyView view = viewResolver(mv.getViewName());
        view.render(mv.getModel(), request, response);
    }


    // 핸들러 매핑이 저장된 handlerMappginMap에서 uri에 해당하는 핸들러(컨트롤러) 객체를 꺼내서 리턴한다.
    private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return handlerMappingMap.get(requestURI);
    }


    // 핸들러 어댑터를 조회하자.
    private MyHandlerAdapter getHandlerAdapter(Object handler) {
        // 해당 핸들러를 처리할 수 있는 어댑터를 찾기 위해
        // 어댑터 정보가 저장된 handlerAdapters 리스트에서 탐색한다.
        // 이후 .supports()를 통해서 그에 맞는 핸들러 어댑터 객체를 반환한다.
        // ex) handler가 controllerV3 인터페이스를 구현함 -> ControllerV3HandlerAdapter 객체 반환
        for (MyHandlerAdapter adapter : handlerAdapters) {
            // 조회한 어댑터가 해당 핸들러를 지원하는가?
            if (adapter.supports(handler)) {
                return adapter;
            }
        }
        // 해당 어댑터를 찾을 수 없다면 에러 반환
        throw new IllegalArgumentException("handler adapter를 찾을 수 없습니다. handler=" + handler);
    }


    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }
}
