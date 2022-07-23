package hello.advanced.trace.strategy;

import hello.advanced.trace.strategy.code.strategy.ContextV1;
import hello.advanced.trace.strategy.code.strategy.Strategy;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic1;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

// 전략 패턴
// 변하지 않는 부분을 context로, 변하는 부분을 strategy라는 인터페이스로 만들기
// 이후, 인터페이스를 구현하는 식으로 해결. 상속(x), 위임(o)

@Slf4j
public class ContextV1Test {

    @Test
    void strategyV0() {
        logic1();
        logic2();
    }

    private void logic1() {
        long startTime = System.currentTimeMillis();
        // 비즈니스 로직 실행
        log.info("비즈니스 로직1 실행");
        // 비즈니스 로직 종료
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }

    private void logic2() {
        long startTime = System.currentTimeMillis();
        // 비즈니스 로직 실행
        log.info("비즈니스 로직2 실행");
        // 비즈니스 로직 종료
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }

    // 전략 패턴 적용
    @Test
    void strategyV1() {
        StrategyLogic1 strategyLogic1 = new StrategyLogic1();
        // 의존관계 주입을 통해서 strategyLogic1을 주입하였음
        ContextV1 contextV1 = new ContextV1(strategyLogic1);
        contextV1.execute(); // 실행 -> 주입받은 strategy에 해당하는 로직 실행 (비즈니스 로직1)

        StrategyLogic2 strategyLogic2 = new StrategyLogic2();
        ContextV1 contextV2 = new ContextV1(strategyLogic2);
        contextV2.execute();
        /*
        17:53:19.633 [main] INFO hello.advanced.trace.strategy.code.strategy.StrategyLogic1 - 비즈니스 로직 1 실행
        17:53:19.644 [main] INFO hello.advanced.trace.strategy.code.strategy.ContextV1 - resultTime=16
        17:53:19.668 [main] INFO hello.advanced.trace.strategy.code.strategy.StrategyLogic2 - 비즈니스 로직 2 실행
        17:53:19.668 [main] INFO hello.advanced.trace.strategy.code.strategy.ContextV1 - resultTime=0
        */
    }

    // 익명 내부 클래스를 활용한 전략 패턴
    @Test
    void strategyV2() {
        Strategy strategyLogic1 = new Strategy() {
            @Override
            public void call() {
                log.info("비즈니스 로직1 실행");
            }
        };
        ContextV1 contextV1 = new ContextV1(strategyLogic1);
        contextV1.execute();

        Strategy strategyLogic2 = new Strategy() {
            @Override
            public void call() {
                log.info("비즈니스 로직2 실행");
            }
        };

        // 이런 식으로 끝에 $가 붙어있는 걸 볼 수 있는데, 이는 익명 내부 클래스가 만들어졌음을 의미한다!
        // 18:01:46.607 [main] INFO hello.advanced.trace.strategy.ContextV1Test - strategyLogic2=class hello.advanced.trace.strategy.ContextV1Test$2
        log.info("strategyLogic2={}", strategyLogic2.getClass());
        ContextV1 context2 = new ContextV1(strategyLogic2);
        context2.execute();
    }

    @Test
    void strategyV3() {
        // 분리 안 하구 이렇게 바로 주입해서 사용할 수도 있다!
        ContextV1 context1 = new ContextV1(new Strategy() {
            @Override
            public void call() {
                log.info("비즈니스 로직1 실행");
            }
        });
        context1.execute();
        ContextV1 context2 = new ContextV1(new Strategy() {
            @Override
            public void call() {
                log.info("비즈니스 로직2 실행");
            }
        });
        context2.execute();
    }

    // 람다를 통한 전략 패턴
    @Test
    void strategyV4() {
        ContextV1 context1 = new ContextV1(() -> log.info("비즈니스 로직 1 실행"));
        context1.execute();

        ContextV1 context2 = new ContextV1(() -> log.info("비즈니스 로직2 실행"));
        context2.execute();
    }

    /**
     * 전략 패턴 ==> 선 조립, 후 실행!
     * 기본적으로 Context의 내부 필드에 Strategy를 두고 사용한다.
     * 이는 Context와 Strategy를 실행 전에 원하는 모양으로 조립하고,
     * 그 다음에 Context를 실행한다.
     *
     * 단, 조립한 이후에는 전략을 변경하기가 번거롭다.
     * Context를 싱글톤으로 사용하면 특히 동시성 이슈를 고려해야 한다는 점!
     */

}
