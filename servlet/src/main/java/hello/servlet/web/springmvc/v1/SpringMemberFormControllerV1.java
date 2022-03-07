package hello.servlet.web.springmvc.v1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

// @Controller는 내부에 @Component 덕분에 자동으로 컴포넌트 스캔의 대상이 되어서
// 자동으로 스프링 빈으로 등록을 시켜준다.
// 추가적으로, @Controller는 @RequestMapping과 더불어서
// RequestMappingHandlerMapping (핸들러 매핑)에게 매핑 정보를 주는 역할도 한다!
@Controller
// 그래서 @Controller = @Component + @RequestMapping으로도 바꿔쓸 수 있다.
// 혹은, @RequestMapping만 두고 빈을 직접 등록해도 된다. (servletApplication 참고)
public class SpringMemberFormControllerV1 {

    // 요청 정보를 매핑해준다.
    // 파라미터로 받은 url이 호출되면 해당 메서드가 호출이 된다.
    // 이때 리턴값으로 model과 view 정보를 받아서 리턴한다.
    @RequestMapping("/springmvc/v1/members/new-form")
    public ModelAndView process() {
        // 뷰의 논리 이름을 파라미터로 넘겨준다.
        return new ModelAndView("new-form");
    }

}
