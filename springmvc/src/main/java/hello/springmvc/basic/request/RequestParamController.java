package hello.springmvc.basic.request;

import hello.springmvc.basic.HelloData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Controller
public class RequestParamController {
    // 가장 기본적인 방식. httpServletRequest가 제공하는 getParameter 사용하기
    @RequestMapping("/request-param-v1")
    public void requestParamV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        // username=team, age=12
        log.info("username={}, age={}", username, age);

        response.getWriter().write("ok");
    }

    // @RequestParam 사용하기.
    // @ResponseBody는 view 조회를 무시하고 http message body에 직접 해당 내용을 입력할 수 있다.
    // 혹은 controller 대신에 restController를 써줘도 된다.
    @ResponseBody
    @RequestMapping("/request-param-v2")
    public String requestParamV2 (
            // 파라미터 이름으로 바인딩하기
            // 여기서 username, age를 통해서 나중에 request.getParameter로 꺼낼 수 있다.
            @RequestParam("username") String memberName,
            @RequestParam("age") int memberAge) {
        log.info("username={}, age={}", memberName, memberAge);
        return "ok";
    }

    // http 파라미터 이름과 변수명이 같으면 생략해주기
    @ResponseBody
    @RequestMapping("/request-param-v3")
    public String requestParamV3 (
           @RequestParam String memberName,
            @RequestParam int memberAge) {
        log.info("username={}, age={}", memberName, memberAge);
        return "ok";
    }

    // string, integer, int 형 같은 단순 타입은 @Requestparam 생략 가능
    // 단, 이때 required=false가 적용된다.
    @ResponseBody
    @RequestMapping("/request-param-v4")
    public String requestParamV4 (String username, int age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }


    // required는 파라미터 필수 여부를 의미한다.
    // 이 값이 무조건 들어와야 하는지, 안 들어와도 되는지. 기본값은 true.
    // 즉, 아래 예시에서는 username이 꼭 들어와야 한다.
    // 단, /username= 이렇게만 헤도 빈 문자로 들어왔다고 인식한다는 점!
    // 그러나, age의 경우 int형이라 null 불가능 (Integer로 하거나 defaultValue로 바꿔야 함)
    @ResponseBody
    @RequestMapping("/request-param-required")
    public String requestParamRequired(
            @RequestParam(required = true) String username,
            @RequestParam(required = false) Integer age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }

    // 파라미터에 값이 없는 경우 기본값을 설정할 수 있다.
    // 사실상 required는 의미가 없는 것.
    // 빈 문자를 넣었을 때도 기본값이 적용된다.
    @ResponseBody
    @RequestMapping("/request-param-default")
    public String requestParamDefault(
            @RequestParam(required = true, defaultValue = "guest") String username,
            @RequestParam(required = false, defaultValue = "-1") int age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }

    // map으로 파라미터 조회하기
    // 보통은 하나의 key 값에 여러 value 값이 들어올 수 있어서 MultiValueMap으로 조회하는 게 낫다.
    @ResponseBody
    @RequestMapping("/request-param-map")
    public String requestParamMap(@RequestParam Map<String, Object> paramMap) {
        // get으로 꺼내오기 (key 값)
        log.info("username={}, age={}",
                paramMap.get("username"),
                paramMap.get("age"));
        return "ok";
    }

    // 이번엔 modelAttribute를 적용해보자.
    @ResponseBody
    @RequestMapping("/model-attribute-v1")
    // @ModelAttribute를 적용하게 되면,
    // HelloData 객체를 생성한 다음, (원래였으면 우리가 직접 new로 만든 다음 set으로 requestParam의 값들을 넣어줬었을 것이다)
    // 요청 파라미터의 이름으로 helloData 객체의 프로퍼티를 찾아서 setter 호출 후
    // 파라미터의 값을 바인딩해준다.
    // ex) http://localhost:8080/model-attribute-v1?username=hello&age=20
    // 이러면 username => setUsername(hello) / age => setAge(20) 이런 식으로 들어가게 되는 것.
    public String modelAttributeV1(@ModelAttribute HelloData helloData) {
        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());
        return "ok";
    }


    // modelAttribute는 새얅이 가능하다.
    // String, int 같은 단순 타입에서는 @RequestParam을 적용하고,
    // 그 나머지에는 ModelAttribute가 적용되는 형태. (argumentResolver는 생략)
    @ResponseBody
    @RequestMapping("/model-attribute-v2")
    public String modelAttributeV2(HelloData helloData) {
        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());
        return "ok";
    }
}
