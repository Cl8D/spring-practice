package hello.advanced.trace.hellotrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
// 메서드 호출의 깊이를 표현하기 위해서
// 첫 로그에서 사용한 트랜잭션ID, level을 다음 로그에 넘겨주기 위해
// 파라미터로 전달을 해준다.
// = 즉, traceId를 다음 로그에 넘겨주자.
public class HelloTraceV2 {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    // 로그 시작 시 호출
    public TraceStatus begin(String message) {
        TraceId traceId = new TraceId();
        Long startTimeMs = System.currentTimeMillis();

        // [796bccd9] |-->OrderService.orderItem()
        // 이런 형태로 로그가 출력된다.
        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);

        return new TraceStatus(traceId, startTimeMs, message);
    }

    /******* V2에서 추가된 부분 ********/
    public TraceStatus beginSync(TraceId beforeTraceId, String message) {
        // 해당 트랜잭션 id를 가지면서 level이 한칸 증가한 형태의 TraceId 리턴
        TraceId nextId = beforeTraceId.createNextId();
        Long startTimeMs = System.currentTimeMillis();
        log.info("[" + nextId.getId() + "] " + addSpace(START_PREFIX,
                nextId.getLevel()) + message);

        return new TraceStatus(nextId, startTimeMs, message);
    }


    // 로그 종료 시 호출
    public void end(TraceStatus status){
        complete(status, null);
    }

    // 예외가 터졌을 때 호출
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

    private void complete(TraceStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();

        // 현재 시간에서 시작 시간을 빼서 총 얼마 걸렸는지 계산
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();

        // traceId 뽑기
        TraceId traceId = status.getTraceId();

        // 예외가 없다면
        if (e == null) {
            log.info("[{}] {}{} time={}ms", traceId.getId(),
                    addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage(),
                    resultTimeMs);
        } else {
            // 예외가 있다면
            log.info("[{}] {}{} time={}ms ex={}", traceId.getId(),
                    addSpace(EX_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs,
                    e.toString());
        }
    }

    /*
    prefix: -->
    level 0:
    level 1: |-->
    level 2: | |-->
    prefix: <--
    level 0:
    level 1: |<--
    level 2: | |<--
    prefix: <X-level 0:
    level 1: |<X-level 2: | |<X-
    */
    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append( (i == level - 1) ? "|" + prefix : "| ");
        }
        return sb.toString();
    }


}
