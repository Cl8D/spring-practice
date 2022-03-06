package hello.servlet.web.frontcontroller.v5;

import hello.servlet.web.frontcontroller.v2.ModelView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface MyHandlerAdapter {

    // 여기서 handler는 컨트롤러를 의미한다.
    // boolean형으로 선언한 이유는, 어댑터가 해당 컨트롤러를 처리할 수 있는지 판단하기 위해서이다.
    // ex) 만약 controllerV3가 입력으로 왔다면 이를 처리할 수 있는 어댑터를 꺼내기 위해 판단하도록
    boolean supports(Object handler);

    // 어댑터는 실제 컨트롤러를 호출하고, modelView를 반환한다.
    // 원래는 프론트 컨트롤러가 컨트롤러를 호출했었지만,
    // 이제는 어댑터를 통해 실제 컨트롤러가 호출된다.
    ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException;
}
