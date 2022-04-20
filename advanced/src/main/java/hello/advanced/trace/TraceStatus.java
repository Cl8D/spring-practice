package hello.advanced.trace;

// 로그의 상태 정보를 나타내는 클래스
// 정확히는 로그를 시작할 때의 상태 정보를 가지고 있음.
public class TraceStatus {

    private TraceId traceId;
    // 어차피 시작 시간만 알면 종료 시간은 계산할 수 있으니까
    private Long startTimeMs;
    // 시작시에 사용된 메시지. 로그 종료시에도 함께 사용한다.
    private String message;

    public TraceStatus(TraceId traceId, Long startTimeMs, String message) {
        this.traceId = traceId;
        this.startTimeMs = startTimeMs;
        this.message = message;
    }

    public TraceId getTraceId() {
        return traceId;
    }

    public Long getStartTimeMs() {
        return startTimeMs;
    }

    public String getMessage() {
        return message;
    }
}
