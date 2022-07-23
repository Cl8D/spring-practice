package hello.advanced.trace.strategy;

import hello.advanced.trace.strategy.code.strategy.ContextV2;
import hello.advanced.trace.strategy.code.strategy.Strategy;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic1;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ContextV2Test {
    @Test
    void strategyV1() {
        // context를 실행할 때마다 전략을 파라미터로 전달하기.
        ContextV2 context = new ContextV2();
        context.execute(new StrategyLogic1());
        context.execute(new StrategyLogic2());
    }

    @Test
    void strategyV2() {
        ContextV2 context = new ContextV2();
        context.execute(new Strategy() {
            @Override
            public void call() {
                log.info("비즈니스 로직1 실행");
            }
        });
        context.execute(new Strategy() {
            @Override
            public void call() {
                log.info("비즈니스 로직2 실행");
            }
        });

    }

    @Test
    void strategyV3() {
        ContextV2 context = new ContextV2();
        context.execute(() -> log.info("비즈니스 로직1 실행"));
        context.execute(() -> log.info("비즈니스 로직2 실행"));
    }
    /**
     * 전략 패턴 2 - 파라미터로 전달받기
     * 유연하게 바꿀 수는 있지만, 실행할 때마다 바꿔야 함...
     *
     * contextV2는 일종의 변하지 않는 템플릿 역할!
     * 파라미터로 넘겨주면서 실행 가능한 코드가 바로 콜백(callback)
     *
     * - callback은 코드가 call 되는데 코드를 넘겨준 곳의 뒤에서(back) 실행된다.
     */
}