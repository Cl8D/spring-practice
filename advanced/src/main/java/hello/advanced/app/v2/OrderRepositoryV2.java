package hello.advanced.app.v2;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryV2 {

    private final HelloTraceV2 trace;

    public void save(TraceId traceId, String itemId) {
        TraceStatus status = null;

        try {
            // service에서 전달받은 traceId를 그대로 사용한다. (level 증가)
            status = trace.beginSync(traceId,"OrderRepository.save()");

            // 저장 로직
            // 아이템 id가 ex면 예외 발생
            if (itemId.equals("ex")){
                throw new IllegalStateException("예외 발생");
            }
            // 상품을 저장하는데 걸리는 시간이 1초 정도라고 가정
            sleep(1000);

            // trace 종료
            trace.end(status);
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }


    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
