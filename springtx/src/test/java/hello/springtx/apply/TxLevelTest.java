package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
public class TxLevelTest {

    // 스프링은 항상 더 구체적이고 자세한 것이 높은 우선순위를 가진다.
    // 트랜잭션도 메서드 > 클래스, 메서드가 더 구체적이니까 더 높은 우선순위를 가진다.
    @Autowired
    LevelService levelService;

    @Test
    void orderTest() {
        levelService.write();
        levelService.read();
    }


    @TestConfiguration
    static class TxApplyLevelConfig {
        @Bean
        LevelService levelService() {
            return new LevelService();
        }
    }


    @Slf4j
    @Transactional(readOnly = true)
    static class LevelService {

        // 메서드가 구체적이기 때문에 readOnly=false가 적용된다.
        @Transactional
        public void write() {
            log.info("call write");
            // tx active=true
            // tx readOnly=false
            printTxInfo();
        }

        // 아무것도 없으니 상위에 있는 트랜잭션 옵션을 따른다. (readOnly=true)
        public void read() {
            log.info("call read");
            // tx active=true
            // tx readOnly=true
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("tx readOnly={}", readOnly);
        }
    }

    /*
        cf) 인터페이스에 @Transactional. (높은 -> 낮은 순서)
        1. 클래스의 메서드
        2. 클래스의 타입
        3. 인터페이스의 메서드
        4. 인터페이스의 타입
        - 걍 클래스가 없으면 인터페이스로 가서 찾는다고 생각하면 된다!
        - 사실 인터페이스에는 @Transactional을 다는 건 좋지 않기 때문에 구체 클래스에 하자.
     */
}
