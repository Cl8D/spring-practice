package hello.servlet.basic;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 서블릿 사용 시 httpServlet 상속받기 필수
// 여기서 name은 서블릿 이름, urlPatterns는 URL 매핑을 의미한다.
@WebServlet(name="helloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {

    // 서블릿 호출 시 service() 메서드가 호출된다.
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 이 부분을 삭제해야 페이지가 정상적으로 실행된다.
        //super.service(req, resp);
        System.out.println("HelloServlet.service");

        // HTTP 요청이 오면, WAS는 request-response 객체를 만들어서 서버에게 전달한다,
        // 사용자가 url을 호출하면, 웹 브라우저가 http 요청 메시지를 만든 다음 그걸 서버에게 줌.
        // 각각이 뭔지 출력을 해보면
        // request = org.apache.catalina.connector.RequestFacade@4c077bd1
        // response = org.apache.catalina.connector.ResponseFacade@65a327e1
        // 톰캣 쪽 라이브러리로, WAS가 구현한 서블릿 표준 객체이다.
        System.out.println("request = " + request);
        System.out.println("response = " + response);

        // http 쿼리 파라미터를 읽어보자.
        // http://localhost:8080/hello?username="kim" 라고 접속했을 때,
        // username = "kim" 라고 성공적으로 출력된다.
        String username = request.getParameter("username");
        System.out.println("username = " + username);

        // 응답 메시지를 보내보자.
        // http content-type (header)에 들어가는 부분
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        // http message body에 들어가는 부분
        response.getWriter().write("hello " + username);
        // 실제로 f12-network로 확인해보면 response에 "hello kim"이 들어가 있다.
    }
}
