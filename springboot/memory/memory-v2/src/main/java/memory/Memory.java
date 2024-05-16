package memory;

import lombok.Getter;
import lombok.ToString;

/**
 * 실시간으로 자바의 메모리 사용량을 확인하기 위한 클래스
 * 별도의 모듈이라고 생각하기 위해서 패키지 분리
 */
@Getter
@ToString
public class Memory {
    private long used;
    private long max;

    public Memory(final long used, final long max) {
        this.used = used;
        this.max = max;
    }
}
