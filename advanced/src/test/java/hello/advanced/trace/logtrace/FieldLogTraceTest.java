package hello.advanced.trace.logtrace;

import hello.advanced.trace.TraceStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldLogTraceTest {
    FieldLogTrace trace = new FieldLogTrace();

    @Test
    void begin_end_level2() {
        TraceStatus status1 = trace.begin("hello1");
        TraceStatus status2 = trace.begin("hello2");
        trace.end(status2);
        trace.end(status1);
    }
    /*
        15:19:27.284 [main] INFO hello.advanced.trace.logtrace.FieldLogTrace - [2e8a08f5] hello1
        15:19:27.349 [main] INFO hello.advanced.trace.logtrace.FieldLogTrace - [2e8a08f5] |-->hello2
        15:19:27.349 [main] INFO hello.advanced.trace.logtrace.FieldLogTrace - [2e8a08f5] |<--hello2 time=30ms
        15:19:27.349 [main] INFO hello.advanced.trace.logtrace.FieldLogTrace - [2e8a08f5] hello1 time=76ms
     */

    @Test
    void begin_exception_level2() {
        TraceStatus status1 = trace.begin("hello");
        TraceStatus status2 = trace.begin("hello2");
        trace.exception(status2, new IllegalStateException());
        trace.exception(status1, new IllegalStateException());
    }

    /*
        15:19:52.108 [main] INFO hello.advanced.trace.logtrace.FieldLogTrace - [2e7e9c8f] hello
        15:19:52.185 [main] INFO hello.advanced.trace.logtrace.FieldLogTrace - [2e7e9c8f] |-->hello2
        15:19:52.186 [main] INFO hello.advanced.trace.logtrace.FieldLogTrace - [2e7e9c8f] |<X-hello2 time=6ms ex=java.lang.IllegalStateException
        15:19:52.186 [main] INFO hello.advanced.trace.logtrace.FieldLogTrace - [2e7e9c8f] hello time=83ms ex=java.lang.IllegalStateException
     */

}