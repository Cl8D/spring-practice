package hello.servlet.web.frontcontroller.v1;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ControllerV1 {
    // 기존의 서블릿과 모양이 동일한 인터페이스 만들기
    // 각각의 컨트롤러들은 이 인터페이스를 구현할 것이고,
    // 프론트 컨트롤러는 이 인터페이스를 호출하여 구현과 관계없이 로직의 일관성을 가져가게 된다.

    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}
