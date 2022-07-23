package hello.proxy.app.v3;

import org.springframework.stereotype.Repository;

/**
 * V3 - 컴포넌트 스캔으로 스프링 빈 자동 등록
 */
@Repository
public class OrderRepositoryV3 {
    public void save(String itemId) {
        // 저장 로직
        if(itemId.equals("ex"))
            throw new IllegalArgumentException("예외 발생!");
        sleep(1000);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();;
        }
    }
}
