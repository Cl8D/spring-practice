package hello.advanced.trace.logtrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadLocalLogTrace implements LogTrace {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    // 스레드 로컬 사용하기!
    private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    // 로그 시작 시 호출
    @Override
    public TraceStatus begin(String message) {
        syncTraceId();
        TraceId traceId = traceIdHolder.get();
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
        TraceId traceId = traceIdHolder.get();
        if(traceId == null)
            traceIdHolder.set(new TraceId());
        else
            traceIdHolder.set(traceId.createNextId());
    }

    // 로그 종료 시 호출
    // 처음이면 제거해주고, 아니면 level-1
    private void releaseTraceId() {
        TraceId traceId = traceIdHolder.get();
        if(traceId.isFirstLevel())
            // 스레드 로컬을 모두 사용했을 때는 꼭 저장한 값을 제거해야 한다!
            // 스레드 풀을 사용하는 경우, remove를 하지 않고 스레드를 반납했을 때
            // 다음 사용자가 해당 스레드를 할당받았을 때 이전 사용자의 데이터를 확인할 수도 있다...!
            traceIdHolder.remove();
        else
            traceIdHolder.set(traceId.createPreviousId());
    }

    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append( (i == level - 1) ? "|" + prefix : "| ");
        }
        return sb.toString();
    }


}
