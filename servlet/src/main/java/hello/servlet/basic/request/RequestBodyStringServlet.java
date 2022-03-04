package hello.servlet.basic.request;

import org.springframework.util.StreamUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "requestBodyStringServlet", urlPatterns = "/request-body-string")
public class RequestBodyStringServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 메시지 바디의 내용을 바이트 코드로 바로 얻을 수 있음
        ServletInputStream inputStream = request.getInputStream();
        // 바이트 코드를 문자로 바꿔줌 (utf-8 형태로)
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        // postman을 이용해서 데이터 post 해줌
        // 결과
        // messageBody = hello!
        System.out.println("messageBody = " + messageBody);

        response.getWriter().write("ok");
    }
}
