package hello.proxy.pureproxy.decorator.code;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DecoratorPatternClient {
    private final Component component;

    // 마찬가지로 클라이언트는 인터페이스만 의존한다.
    public void execute() {
        String result = component.operation();
        log.info("result={}", result);
    }
}
