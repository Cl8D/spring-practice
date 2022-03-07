package hello.servlet.web.springmvc.old;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 스프링 빈의 이름을 /springmvc/old-controller 이름으로 등록하자.
// 즉, 빈의 이름으로 url을 매핑할 예정
@Component("/springmvc/old-controller")
public class OldController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 이 컨트롤러가 호출되는지 확인
        // http://localhost:8080/springmvc/old-controller에 들어가면 호출이 되는 걸 확인할 수 있다.
        System.out.println("OldController.handleRequest");
        // WEB-INF의 new-form.jsp로 가도록! (논리적 이름)
        return new ModelAndView("new-form");
    }
}
