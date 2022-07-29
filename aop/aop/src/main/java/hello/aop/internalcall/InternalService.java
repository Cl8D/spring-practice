package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// 이렇게 별도의 클래스로 분리하기
@Slf4j
@Component
public class InternalService {
    public void internal() {
        log.info("call internal");
    }
}
