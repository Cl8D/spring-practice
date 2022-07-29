package hello.aop.internalcall;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 내부 호출이 일어나지 않도록 구조를 바꾸기 (사실 이걸 제일 권장)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallServiceV3 {
    private final InternalService internalService;

    public void external() {
        log.info("call external");
        // 별도의 클래스로 분리하기
        internalService.internal();
    }
}
