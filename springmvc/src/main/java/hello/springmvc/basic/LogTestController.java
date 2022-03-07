package hello.springmvc.basic;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// restController를 사용하면 return으로 준 string 값이 그대로 반환된다.
// 기존 controller는 string으로 반환되면 뷰 이름이라고 인식했지만,
// restController의 경우 http 메시지 바디에 바로 입력된다.
@RestController
@Slf4j
public class LogTestController {
    // 현재 내 클래스를 파라미터로 넣어주기
    // 롬복의 Slf4j를 사용하면 생략 가능
    //private final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping("/log-test")
    private String logTest() {
        String name = "Spring";

        // 2022-03-07 16:37:21.643  INFO 16964 --- [nio-8080-exec-1] hello.springmvc.basic.LogTestController  :  info log=Spring
        // 이런 식으로 콘솔에 로그가 찍힌다.
        // 시간, 로그 레벨, 프로세스 ID, 쓰레드 명, 클래스명, 로그 메시지
        log.info(" info log={}", name);

        // 로그 레벨 정하기
        // 2022-03-07 16:42:20.175  WARN 16964 --- [nio-8080-exec-1] hello.springmvc.basic.LogTestController  :  warn log=Spring
        // 2022-03-07 16:42:20.175 ERROR 16964 --- [nio-8080-exec-1] hello.springmvc.basic.LogTestController  : error log=Spring
        // trace부터 볼지 - debug부터 볼지...? (얘들은 디폴트로는 보이지 않는다)
        log.trace("trace log={}", name);
        log.debug("debug log={}", name);
        log.warn(" warn log={}", name);
        log.error("error log={}", name);

        return "ok";
    }
}
