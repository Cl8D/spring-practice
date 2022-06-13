package hello.advanced.trace.logtrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class FieldLogTrace implements LogTrace {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    // 파라미터 대신에 필드에 저장되도록 -> 단, 동시성 이슈가 발생함.
    private TraceId traceIdHolder;

    // 로그 시작 시 호출
    @Override
    public TraceStatus begin(String message) {
        syncTraceId();
        TraceId traceId = traceIdHolder;
        Long startTimeMs = System.currentTimeMillis();

        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);

        return new TraceStatus(traceId, startTimeMs, message);
    }


    // 로그 종료 시 호출
    @Override
    public void end(TraceStatus status){
        complete(status, null);
    }

    // 예외가 터졌을 때 호출
    @Override
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

        releaseTraceId();
    }

    // 로그 시작 시 호출
    // 비어있으면 새로 만들고, 이미 존재하면 그전 것보다 깊이를 +1
    private void syncTraceId() {
        if(traceIdHolder == null)
            traceIdHolder = new TraceId();
        else
            traceIdHolder = traceIdHolder.createNextId();
    }

    // 로그 종료 시 호출
    // 처음이면 제거해주고, 아니면 level-1
    private void releaseTraceId() {
        if(traceIdHolder.isFirstLevel())
            traceIdHolder = null;
        else
            traceIdHolder = traceIdHolder.createPreviousId();
    }

    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append( (i == level - 1) ? "|" + prefix : "| ");
        }
        return sb.toString();
    }


}
