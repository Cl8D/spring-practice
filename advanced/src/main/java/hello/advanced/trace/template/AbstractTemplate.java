package hello.advanced.trace.template;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;

// 부모 클래스로, 템플릿 역할. <T>를 통해 반환타입 정의.
@RequiredArgsConstructor
public abstract class AbstractTemplate<T> {
    private final LogTrace trace; // 내부에서 사용할 LogTrace

    public T execute(String message) {
        TraceStatus status = null;
        try {
            status = trace.begin(message);
            // 로직 호출 - 변하는 부분
            T result = call();
            trace.end(status);
            return result;
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }

    // 변하는 부분을 처리하는 메서드 - 상속으로 구현 필요
    protected abstract T call();
}
