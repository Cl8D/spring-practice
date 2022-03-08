package hello.springmvc.basic.response;

import hello.springmvc.basic.HelloData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
// cf)
// @RestController = @Controller + @ResponseBody
public class ResponseBodyController {

    //HttpServletResponse 객체를 통해서 http 메시지 바디에 직접 ok 응답 메시지 전달
    @GetMapping("/response-body-string-v1")
    public void responseBodyV1 (HttpServletResponse response) throws IOException {
        response.getWriter().write("ok");
    }

    // ResponseEntity는 HTTPEntity를 상속받았다.
    // HTTP 메시지 헤더, 바디 + 응답 코드 설정 가능
    @GetMapping("/response-body-string-v2")
    public ResponseEntity<String> responseBodyV2 () {
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    // 이러면 view를 사용하지 않고 http 메시지 컨버터를 통해서
    // http 메시지를 직접 입력할 수 있게 된다.
    @ResponseBody
    @GetMapping("/response-body-string-v3")
    public String responseBodyV3() {
        return "ok";
    }

    // http 메시지 컨버터를 통해 json 형식으로 변환되어 반환됨
    @GetMapping("/response-body-json-v1")
    public ResponseEntity<HelloData> responseBodyJsonV1() {
        HelloData helloData = new HelloData();
        helloData.setUsername("userA");
        helloData.setAge(20);

        return new ResponseEntity<>(helloData, HttpStatus.OK);
    }


    // 애노테이션을 통한 응답 코드 설정 (단, 정적 설정이라는 점 참고)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/response-body-json-v2")
    public HelloData responseBodyJsonV2() {
        HelloData helloData = new HelloData();
        helloData.setUsername("userA");
        helloData.setAge(20);

        return helloData;
    }

}
