package hello.advanced.trace.hellotrace;

import hello.advanced.trace.TraceStatus;
import org.junit.jupiter.api.Test;

class HelloTraceV2Test {


    @Test
    public void begin_end_level2() throws Exception {
        HelloTraceV2 trace = new HelloTraceV2();
        TraceStatus status1 = trace.begin("hello1");
        TraceStatus status2 = trace.beginSync(status1.getTraceId(), "hello2");
        trace.end(status2);
        trace.end(status1);
    }
    /*
    23:03:47.877 [main] INFO helllo.advanced.trace.hellotrace.HelloTraceV2 - [069638c1] hello1
    23:03:48.010 [main] INFO helllo.advanced.trace.hellotrace.HelloTraceV2 - [069638c1] |-->hello2
    23:03:48.010 [main] INFO helllo.advanced.trace.hellotrace.HelloTraceV2 - [069638c1] |<--hello2 time=113ms
    23:03:48.010 [main] INFO helllo.advanced.trace.hellotrace.HelloTraceV2 - [069638c1] hello1 time=140ms
     */


    @Test
    public void begin_exception_level2() throws Exception {
        HelloTraceV2 trace = new HelloTraceV2();
        TraceStatus status1 = trace.begin("hello1");
        TraceStatus status2 = trace.beginSync(status1.getTraceId(), "hello2");
        trace.exception(status2, new IllegalStateException());
        trace.exception(status1, new IllegalStateException());
    }
    /*
    23:04:55.185 [main] INFO helllo.advanced.trace.hellotrace.HelloTraceV2 - [29795b72] hello1
    23:04:55.257 [main] INFO helllo.advanced.trace.hellotrace.HelloTraceV2 - [29795b72] |-->hello2
    23:04:55.257 [main] INFO helllo.advanced.trace.hellotrace.HelloTraceV2 - [29795b72] |<X-hello2 time=57ms ex=java.lang.IllegalStateException
    23:04:55.258 [main] INFO helllo.advanced.trace.hellotrace.HelloTraceV2 - [29795b72] hello1 time=79ms ex=java.lang.IllegalStateException
     */

}