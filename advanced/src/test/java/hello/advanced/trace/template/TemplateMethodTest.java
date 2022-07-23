package hello.advanced.trace.template;

import hello.advanced.trace.code.AbstractTemplate;
import hello.advanced.trace.template.code.SubClassLogic1;
import hello.advanced.trace.template.code.SubClassLogic2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

// 템플릿 메세드 패턴
// 부모클래스에 변하지 않는 템플릿을 두고, 변하는 부분은 자식 클래스에 둔 다음 상속으로 구현하기
@Slf4j
public class TemplateMethodTest {

    @Test
    void templateMethodV0() {
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

    // 여기서 변하는 부분 = 비즈니스 로직,
    // 변하지 않는 부분 = 시간 측정 코드
    // 이 둘을 분리해보자.
    /**
     * 템플릿 메서드 패턴 적용
     */
    @Test
    void templateMethodV1() {
        AbstractTemplate template1 = new SubClassLogic1();
        template1.execute();

        AbstractTemplate template2 = new SubClassLogic2();
        template2.execute();

        /*
            18:06:27.380 [main] INFO hello.advanced.trace.template.code.SubClassLogic1 - 비즈니스 로직 1 실행
            18:06:27.388 [main] INFO hello.advanced.trace.code.AbstractTemplate - resultTime=14
            18:06:27.392 [main] INFO hello.advanced.trace.template.code.SubClassLogic2 - 비즈니스 로직 2 실행
            18:06:27.392 [main] INFO hello.advanced.trace.code.AbstractTemplate - resultTime=0

            template1.execute() -> AbstractTemplate.execute() 실행 -> 중간에 call() 호출
            -> 오버라이딩 되어 있기 때문에 현재 인스턴스인 SubClassLogic1 인스턴스의 SubClassLogic1.call() 메서드 호출
         */
    }


    /**
     * 템플릿 메서드 패턴 + 익명 내부 클래스 사용
     */
    @Test
    void templateMethodV2() {
        // 내부 클래스 + 익명 클래스.
        // 익명 클래스 = 클래스의 선언과 객체의 생성을 동시에 하는 이름없는 클래스 (일회용)
        AbstractTemplate template1 = new AbstractTemplate() {
            @Override
            protected void call() {
                log.info("비즈니스 로직1 실행");
            }
        };
        // 클래스 이름1=class hello.advanced.trace.template.TemplateMethodTest$1
        // : 자바가 내부에서 임의로 이름을 만들어주었다. (TemplateMethodTest$1)
        log.info("클래스 이름1={}", template1.getClass());
        template1.execute();

        AbstractTemplate template2 = new AbstractTemplate() {
            @Override
            protected void call() {
                log.info("비즈니스 로직2 실행");
            }
        };
        // 클래스 이름2=class hello.advanced.trace.template.TemplateMethodTest$2
        log.info("클래스 이름2={}", template2.getClass());
        template2.execute();
    }


}
