package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 필드에 전략을 보관하는 방식
 */
// context -> 변하지 않는 로직을 가진 템플릿 역할을 하는 코드
@Slf4j
public class ContextV1 {
    // context는 오직 strategy 인터페이스에만 의존한다.
    private Strategy strategy; // 변하는 부분 주입

    public ContextV1(Strategy strategy) {
        this.strategy = strategy;
    }

    public void execute() {
        long startTime = System.currentTimeMillis();

        //비즈니스 로직 실행
        strategy.call(); //위임
        //비즈니스 로직 종료

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }

}
