package hello.aop.exam;

import hello.aop.exam.annotation.Retry;
import hello.aop.exam.annotation.Trace;
import org.springframework.stereotype.Repository;

@Repository
public class ExamRepository {
    private static int seq = 0;

    /**
     * 5번에 1번 실패하는 요청 - 실패할 경우 재시도하는 AOP 생성하기.
     */
    @Trace
    @Retry(value = 4) // 4번 재시도 할 수 있도록
    public String save(String itemId) {
        seq++;
        if (seq % 5 == 0) {
            throw new IllegalStateException("예외 발생!");
        }
        return "ok";
    }
}
