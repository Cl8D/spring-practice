package hello.springmvc.basic.requestmapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class MappingController {
    private Logger log = LoggerFactory.getLogger(getClass());

    // /hello-basic URL 호출이 오면 이 메서드가 실행되도록 매핑
    // 다중 설정 가능
    // ex) @RequestMapping({"/hello-basic", "/hello-go})
    // 또한, /hello-basic이나 /hello-basic/이나 동일한 요청이라고 판단하고 매핑한다.
    // cf) method를 지정하지 않으면 http method와 상관없이 호출이 됨됨    @RequestMapping("/hello-basic")
    @RequestMapping("/hello-basic")
    public String helloBasic() {
        log.info("helloBasic");
        return "OK";
    }

    // 메서드 지정해주기 (get만 가능)
    @RequestMapping(value = "/mapping-get-v1", method = RequestMethod.GET)
    public String mappingGetV1() {
        log.info("mappingGetV1");
        return "ok";
    }


    /**
     * 편리한 축약 애노테이션 (코드보기)
     * @GetMapping
     * @PostMapping
     * @PutMapping
     * @DeleteMapping
     * @PatchMapping
     */
    // 조금 더 발전시키기 (Getmapping 사용)
    @GetMapping(value = "/mapping-get-v2")
    public String mappingGetV2() {
        log.info("mapping-get-v2");
        return "ok";
    }

    // pathVariable (경로 변수) 사용 (많이 사용함)
    // @PathVariable 이름과 변수명이 동일하면 생략할 수 있다.
    // ex) * @PathVariable("userId") String userId -> @PathVariable userId
    @GetMapping("/mapping/{userId}")
    public String mappingPath(@PathVariable("userId") String data) {
        // 이렇게 하면 url에서 /mapping/userA라고 한다면, data에 userA가 들어가게 된다.
        log.info("mappingPath userId={}", data);
        return "ok";
    }

    // 경로 변수 다중 사용
    // ex) http://localhost:8080/mapping/users/userA/orders/100
    @GetMapping("/mapping/users/{userId}/orders/{orderId}")
    public String mappingPath(@PathVariable String userId,
                              @PathVariable Long orderId) {
        // userId=userA, orderId=100
        log.info("mappingPath userId={}, orderId={}", userId, orderId);
        return "ok";
    }


    // 특정 파라미터가 있거나 없는 조건 확인
    // 얘는 파라미터에 mode-debug가 있어야 실행됨
    // ex) http://localhost:8080/mapping-param?mode=debug
    // 그래서, 단순히 http://localhost:8080/mapping-param로 실행하면 안 된다.
    @GetMapping(value = "/mapping-param", params = "mode=debug")
    public String mappingParam() {
        log.info("mappingParam");
        return "ok";
    }

    // 특정 헤더 조건 매핑
    // postman으로 header를 mode, 값으로 debug를 넣어줘야 실행된다.
    @GetMapping(value = "/mapping-header", headers = "mode=debug")
    public String mappingHeader() {
        log.info("mappingHeader");
        return "ok";
    }

    // 미디어 타입 조건 매핑
    // content-type에 따라서 매핑하는 것
    // 여기서 application/json 타입이 아니면 오류난다.
    // 조건을 consumes으로 줘야 한다.
    @PostMapping(value = "/mapping-consume", consumes = "application/json")
    public String mappingConsumes() {
        log.info("mappingConsumes");
        return "ok";
    }

    /**
     * 참고로, content-type은 데이터의 형식이 무엇인지 표시하는 것이고 (약간 서버 측에서?)
     * Accept은 클라이언트가 선호하는 미디어 타입이 무엇인지 전달하는 것이다.
     */

    // 대신 produces로 해주면
    // http 요청의 accept 헤더를 기반으로 미디어 타입을 매핑한다.
    // accept 헤더가 text/html이어야 한다.
    @PostMapping(value = "/mapping-produce", produces = "text/html")
    public String mappingProduces() {
        log.info("mappingProduces");
        return "ok";
    }


}
