package hello.springmvc.basic.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Slf4j
@RestController
public class RequestHeaderController {

    /**
     * HttpServletRequest
     * HttpServletResponse
     * HttpMethod : HTTP 메서드를 조회한다. org.springframework.http.HttpMethod
     * Locale : Locale 정보를 조회한다.
     * @RequestHeader MultiValueMap<String, String> headerMap
     * -> 모든 HTTP 헤더를 MultiValueMap 형식으로 조회한다.
     * @RequestHeader("host") String host
     * -> 특정 HTTP 헤더를 조회한다.
     * -> 속성)
     *      필수 값 여부: required
     *      기본 값 속성: defaultValue
     * @CookieValue(value = "myCookie", required = false) String cookie
     * -> 특정 쿠키를 조회한다.
     * -> 속성)
     *      필수 값 여부: required
     *      기본 값: defaultValue
     * */
    @RequestMapping("/headers")
    public String headers(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpMethod httpMethod,
                          Locale locale,
                          // multiValueMap의 경우 하나의 키에 여러 value 값을 담을 수 있다.
                          // HTTP 쿼리 파라미터에서 keyA=value1&keyA=value2 이런 경우에서 사용함
                          @RequestHeader MultiValueMap<String, String> headerMap,
                          @RequestHeader("host") String host,
                          @CookieValue(value="myCookie", required = false) String cookie
                          ) {

        // request=org.apache.catalina.connector.RequestFacade@5c6aae51
        log.info("request={}", request);
        // response=org.apache.catalina.connector.ResponseFacade@3caa659d
        log.info("response={}", response);
        // httpMethod=GET
        log.info("httpMethod={}", httpMethod);
        // locale=en_US
        log.info("locale={}", locale);
        // 그냥 헤더 정보 전부가 들어온다 (매우 길다)
        // headerMap={host=[localhost:8080], connection=[keep-alive]...
        log.info("headerMap={}", headerMap);
        // header host=localhost:8080
        log.info("header host={}", host);
        // myCookie=null
        log.info("myCookie={}", cookie);

        return "ok";
    }
}
