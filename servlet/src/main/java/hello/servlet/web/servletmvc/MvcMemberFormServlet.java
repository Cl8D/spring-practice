package hello.servlet.web.servletmvc;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 회원 등록 - 컨트롤러
@WebServlet(name = "mvcMemberFormServlet", urlPatterns = "/servlet-mvc/members/new-form")
public class MvcMemberFormServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 먼저 jsp에게 가주자.
        // /WEB-INF 안에(폴더) JSP가 있으면 외부에서 JSP를 직접 호출할 수 없다.
        // 항상 컨트롤러 JSP를 호출하는 게 이상적.
        String viewPath = "/WEB-INF/views/new-form.jsp";

        // controller -> view로 이동할 때 getRequestDispatcher 사용한다.
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);

        // 다른 서블릿이나 jsp로 이동할 때 forward()를 사용한다.
        // 서버 내부에서 재호출이 발생함.
        // 즉, 이러면 클라이언트에게 다시 가지 않고 발생한다는 것.
        dispatcher.forward(request, response);
    }
}
