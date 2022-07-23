package hello.proxy.pureproxy.decorator.code;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MessageDecorator implements Component{
    private final Component component;

    @Override
    public String operation() {
        log.info("MessageDecorator 실행");
        // 프록시처럼 클라이언트는 데코레이터를 참조하지만, 내부에서는 실제 객체를 참조한다.
        String result = component.operation();
        String decoResult = "*****" + result + "*****";
        // 부가 기능을 추가해주기, 메시지 꾸며주기!
        log.info("MessageDecorator 꾸미기 적용 전={}, 적용 후={}", result,
                decoResult);
        return decoResult;
    }
}
