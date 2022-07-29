package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CallServiceV0 {
    public void external() {
        log.info("call external");
        // external 내부에서 또 다른 함수 호출.
        // this.internal() 이기 때문에 CallServiceV0의 internal()이 호출된다.
        internal();
    }

    public void internal() {
        log.info("call internal");
    }
}
