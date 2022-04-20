package hello.advanced.trace;

import java.util.UUID;

public class TraceId {
    // 트랜잭션 id
    private String id;
    // 메시지 호출의 깊이 (레벨)
    private int level;

    public TraceId() {
        this.id = createId();
        this.level = 0;
    }

    private TraceId(String id, int level) {
        this.id = id;
        this.level = level;
    }

    private String createId() {
        // 랜덤하게 생성된 uuid 중에서 앞부분만 사용하기
        // 트랜잭션 id가 거의 중복되지 않도록
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public TraceId createNextId() {
        // 트랜잭션 id는 똑같은데 해당 트랜잭션에서 다른 함수를 호출하게 되면
        // 레벨이 증가하기 때문에, 그러한 처리를 해주는 함수
        return new TraceId(id, level+1);
    }

    public TraceId createPreviousId() {
        // 레벨을 하나 줄이도록
        return new TraceId(id, level-1);
    }

    // 가장 처음 레벨인지 판단
    public boolean isFirstLevel() {
        return level == 0;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }
}
