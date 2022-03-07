package hello.springmvc.basic.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
public class RequestBodyStringController {
    // 텍스트 메시지를 http message body에 담아서 전송하고 읽어보기
    // inputStream을 사용해서 직접 읽을 수 있음
    @PostMapping("/request-body-string-v1")
    public void requestBodyString(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        // 바이트코드를 문자로 바꾸기
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("messageBody={}", messageBody);

        response.getWriter().write("ok");
    }

    // inputStream과 writer를 파라미터로 받아버리기
    @PostMapping("/request-body-string-v2")
    public void requestBodyStringV2(InputStream inputStream, Writer responseWriter) throws IOException {
        // inputStream(Reader)는 HTTP 요청 메시지 바디의 내용을 직접 조회하기
        // 반대로 outputStream을 사용하면 http 응답 메시지의 바디에 직접 결과 출력 가능
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        log.info("messageBody={}", messageBody);
        responseWriter.write("ok");
    }


    // 더 나아가서 httpEntity를 사용하면 http header/body 정보를 편리하게 조회할 수 있다.
    // 요청 파라미터를 조회하는 기능과 관계 X (requestParam, modelAttribute)
    // HttpEntity<String>이라고 했을 때, String형을 요청했다는 걸 바탕으로 알아서 v2 버전의 copyToString 같은 느낌을 호출해준다
    @PostMapping("/request-body-string-v3")
    public HttpEntity<String> requestBodyStringV3(HttpEntity<String> httpEntity) {
        // 그래서 getBody()를 통해 body를 string 형으로 얻을 수 있다.
        String messageBody = httpEntity.getBody();

        log.info("messageBody={}", messageBody);
        // body 메시지에 "ok"를 담아서 리턴 가능.
        return new HttpEntity<>("ok");
    }

    // 조금 더 구체화할 수 있다.
    // responseBody -> 메시지 바디 정보를 직접 조회 가능
    // requestBody -> 메시지 바디 정보를 직접 반환 가능
    @ResponseBody
    @PostMapping("/request-body-string-v4")
    public String requestBodyStringV4(@RequestBody String messageBody) {
        // 이러면 아예 getBody 필요없이 바로 body를 읽어올 수 있게 된다.
        log.info("messageBody={}", messageBody);
        return "ok";
    }


}
