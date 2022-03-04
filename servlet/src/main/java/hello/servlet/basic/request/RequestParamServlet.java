package hello.servlet.basic.request;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 우리가 할 것.
 * 1. 파라미터 전송 기능
 * http://localhost:8080/request-param?username=hello&age=20
 *
 * 2. 동일한 파라미터 전송 가능
 * http://localhost:8080/request-param?username=hello&username=kim&age=20
 */

@WebServlet(name = "requestParamServlet", urlPatterns = "/request-param")
public class RequestParamServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[전체 파라미터 조회하기] - start");
        request.getParameterNames().asIterator()
                        .forEachRemaining(paramName
                                -> System.out.println("key: " + paramName + " value: " + request.getParameter(paramName)));
        System.out.println("[전체 파라미터 조회하기] - end");

        System.out.println();

        System.out.println("[단일 파라미터 조회하기] - start");
        String username = request.getParameter("username");
        String age = request.getParameter("age");
        System.out.println("username = " + username);
        System.out.println("age = " + age);
        System.out.println("[단일 파라미터 조회하기] - end");

        System.out.println();

        System.out.println("[이름이 같은 복수 파라미터 조회하기] - start");
        String[] usernames = request.getParameterValues("username");
        for (String name : usernames) {
            System.out.println("name = " + name);
        }
        System.out.println("[이름이 같은 복수 파라미터 조회하기] - end");

        // 웹 브라우저에 표시하기 위해 간단하게 적은 거
        response.getWriter().write("ok");
    }
    /*
    * 출력 결과)
    * [전체 파라미터 조회하기] - start
      key: username value: hello
      key: age value: 20
      [전체 파라미터 조회하기] - end
    *
    * [단일 파라미터 조회하기] - start
      username = hello
      age = 20
      [단일 파라미터 조회하기] - end
    *
    * [이름이 같은 복수 파라미터 조회하기] - start
      name = hello
      name = kim
      [이름이 같은 복수 파라미터 조회하기] - end
    * */
}
