package hello.servlet.basic.response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "responseHeaderServlet", urlPatterns = "/response-header")
public class ResponseHeaderServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // http 응답 코드 넣기 가능 (200 - OK)
        // [status-line]
        response.setStatus(HttpServletResponse.SC_OK);

        // [response-header]
        // 내가 원하는 헤더 정보 넣어줄 수 있다.
        response.setHeader("Content-Type", "text/plain");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        // 내가 원하는 임의의 헤더 만들기
        response.setHeader("my-header","hello");

        // [Header 편의 메서드]
        content(response);
        cookie(response);
        redirect(response);

        //[message body]
        PrintWriter writer = response.getWriter();
        writer.println("ok");
    }

    // content 편의 메서드
    private void content(HttpServletResponse response) {
        //Content-Type: text/plain;charset=utf-8
        //Content-Length: 2

        // 이렇게 선언해주는 것 대신에
        //response.setHeader("Content-Type", "text/plain;charset=utf-8");
        // 이런 식으로도 만들어 줄 수 있다 (따로따로)
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        // 필수 요건이어서 지정을 안 해주면 자동으로 생성해준다.
        // writer의 길이라고 볼 수 있음 (ok + println으로 인해 생긴 공백 1칸)
        // response.setContentLength(2);
    }

    // 쿠키 편의 메서드
    private void cookie(HttpServletResponse response) {
        //Set-Cookie: myCookie=good; Max-Age=600;

        // 물론 이런 식으로 세팅할 수 있지만
        //response.setHeader("Set-Cookie", "myCookie=good; Max-Age=600");

        // 쿠기 객체를 만들어서 해줄 수 있다
        Cookie cookie = new Cookie("myCookie", "good");
        cookie.setMaxAge(600); //600초
        response.addCookie(cookie);
    }

    // redirect 편의 메서드
    private void redirect(HttpServletResponse response) throws IOException {
        //Status Code 302
        //Location: /basic/hello-form.html


        // 302 - 리다이렉트. 이런 식으로도 해줄 수 있지만
        //response.setStatus(HttpServletResponse.SC_FOUND); //302
        //response.setHeader("Location", "/basic/hello-form.html");

        // 아예 리다이렉트 메서드가 존재한다.
        response.sendRedirect("/basic/hello-form.html");
    }

}
