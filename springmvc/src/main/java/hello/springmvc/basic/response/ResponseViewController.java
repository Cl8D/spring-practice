package hello.springmvc.basic.response;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

// 뷰 템플릿을 호출하는 컨트롤러
@Controller
public class ResponseViewController {

    @RequestMapping("/response-view-v1")
    public ModelAndView responseViewV1() {
        // 이렇게 해주면 response에 있는 hello.html에 있는
        // <p th:text="${data}">empty</p>
        // 이 부분의 data에 "hello!"가 들어가게 되고, empty의 값이 hello!로 변경된다.
        ModelAndView mav = new ModelAndView("response/hello")
                .addObject("data", "hello!");
        return mav;
    }


    // 기본적으로 string을 반환하게 되면, (+ResponseBody가 없으면)
    // response/hello로 뷰 리졸버가 실행되어서 뷰를 찾아 렌더링을 진행한다.
    // 만약 ResponseBody가 있으면 http 메시지 바디에 response/hello라는 문자가 그대로 입력된다.
    @RequestMapping("/response-view-v2")
    public String responseViewV2(Model model) {
        model.addAttribute("data", "hello!!");
        return "response/hello";
    }


    // void를 반환하고 @Controller를 사용하면서,
    // HttpServletResponse, OutputStream(Writer) 같은 HTTP 메시지 바디를 처리하는 파라미터가 없으면
    // 요청 URL을 참고하여 논리 뷰 이름으로 사용한다. (정확하게는 컨트롤러의 경로 이름과 뷰의 논리 이름이 같다면)
    // 즉, 여기서 /response/hello를 논리 뷰 이름으로 사용하여
    // templates/response/hello.html이 실행되는 것.
    // 그러나, 권장되지 않는 방법이다.
    @RequestMapping("/response/hello")
    public void responseViewV3(Model model) {
        model.addAttribute("data", "hello!!");
    }



}

