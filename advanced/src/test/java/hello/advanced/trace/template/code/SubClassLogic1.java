package hello.advanced.trace.template.code;

import hello.advanced.trace.code.AbstractTemplate;
import lombok.extern.slf4j.Slf4j;

// 변하는 부분을 처리하는 자식 클래스 - call() 메서드를 오버라이딩 해주기
@Slf4j
public class SubClassLogic1 extends AbstractTemplate {
    @Override
    protected void call() {
        log.info("비즈니스 로직 1 실행");
    }
}
