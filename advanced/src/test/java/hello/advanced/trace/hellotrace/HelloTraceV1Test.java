package hello.advanced.trace.hellotrace;

import hello.advanced.trace.TraceStatus;
import org.junit.jupiter.api.Test;


// v1, v2 test의 경우 group 이름이 오타났어서 helllo라고 나온다...ㅎ
class HelloTraceV1Test {
    @Test
    public void begin_end() throws Exception {
        HelloTraceV1 trace = new HelloTraceV1();
        TraceStatus status = trace.begin("hello");
        trace.end(status);
    }
    // 22:31:52.414 [main] INFO helllo.advanced.trace.hellotrace.HelloTraceV1 - [d8211930] hello
    // 22:31:52.425 [main] INFO helllo.advanced.trace.hellotrace.HelloTraceV1 - [d8211930] hello time=18ms

    @Test
    public void begin_exception() throws Exception {
        HelloTraceV1 trace = new HelloTraceV1();
        TraceStatus status = trace.begin("hello");
        trace.exception(status, new IllegalStateException());
    }
    // 22:33:17.660 [main] INFO helllo.advanced.trace.hellotrace.HelloTraceV1 - [cff3bf9d] hello
    // 22:33:17.672 [main] INFO helllo.advanced.trace.hellotrace.HelloTraceV1 - [cff3bf9d] hello time=23ms ex=java.lang.IllegalStateException


}