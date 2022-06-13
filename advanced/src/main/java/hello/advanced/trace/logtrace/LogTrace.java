package hello.advanced.trace.logtrace;

import hello.advanced.trace.TraceStatus;

// 파라미터를 넘기지 않고 traceId를 동기화할 수 있는 로그 추적기 만들기!
public interface LogTrace {
    TraceStatus begin(String message);
    void end(TraceStatus status);
    void exception(TraceStatus status, Exception e);
}
