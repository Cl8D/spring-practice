package hello.advanced.trace.logtrace;

import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ThreadLocalLogTraceTest {
    ThreadLocalLogTrace trace = new ThreadLocalLogTrace();

    @Test
    void begin_end_level2() {
        TraceStatus status1 = trace.begin("hello1");
        TraceStatus status2 = trace.begin("hello2");
        trace.end(status2);
        trace.end(status1);
    }
    /*
        16:13:38.689 [main] INFO hello.advanced.trace.logtrace.ThreadLocalLogTrace - [e631a3f2] hello1
        16:13:38.707 [main] INFO hello.advanced.trace.logtrace.ThreadLocalLogTrace - [e631a3f2] |-->hello2
        16:13:38.708 [main] INFO hello.advanced.trace.logtrace.ThreadLocalLogTrace - [e631a3f2] |<--hello2 time=6ms
        16:13:38.708 [main] INFO hello.advanced.trace.logtrace.ThreadLocalLogTrace - [e631a3f2] hello1 time=24ms
     */

    @Test
    void begin_exception_level2() {
        TraceStatus status1 = trace.begin("hello");
        TraceStatus status2 = trace.begin("hello2");
        trace.exception(status2, new IllegalStateException());
        trace.exception(status1, new IllegalStateException());
    }

    /*
        16:14:17.492 [main] INFO hello.advanced.trace.logtrace.ThreadLocalLogTrace - [ab18fed5] hello
        16:14:17.511 [main] INFO hello.advanced.trace.logtrace.ThreadLocalLogTrace - [ab18fed5] |-->hello2
        16:14:17.511 [main] INFO hello.advanced.trace.logtrace.ThreadLocalLogTrace - [ab18fed5] |<X-hello2 time=6ms ex=java.lang.IllegalStateException
        16:14:17.512 [main] INFO hello.advanced.trace.logtrace.ThreadLocalLogTrace - [ab18fed5] hello time=26ms ex=java.lang.IllegalStateException
     */

}