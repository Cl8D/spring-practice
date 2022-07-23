package hello.proxy.pureproxy.concreteproxy.code;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 구체클래스를 상속받은 프록시.
@Slf4j
@RequiredArgsConstructor
public class TimeProxy extends ConcreteLogic{
    private final ConcreteLogic realLogic;

    // 시간 측정 로직
    @Override
    public String operation() {
        log.info("TimeDecorator 실행");
        long startTime = System.currentTimeMillis();

        // 프록시에서 실제 객체 호출
        String result = realLogic.operation();
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;

        log.info("TimeDecorator 종료 resultTime={}", resultTime);
        return result;
    }
}
