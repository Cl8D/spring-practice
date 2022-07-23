package hello.advanced.trace.callback;

// 제네릭으로 선언해주기
public interface TraceCallback<T> {
    T call();
}
