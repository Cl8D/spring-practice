package hello.proxy.pureproxy.proxy.code;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProxyPatternClient {
    // 클라이언트는 현재 인터페이스에 의존하는 것을 볼 수 있다.
    private final Subject subject;

    public void execute() {
        subject.operation();
    }
}
