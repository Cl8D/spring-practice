package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV1Test {

    @Autowired
    CallService callService;

    @Test
    void printProxy() {
        // @Transactional이 하나라도 있으면 트랜잭션 프록시 객체가 만들어진다.(CallService 내의 internal()에 붙어있음)
        // callService의 클래스를 보면 프록시 객체가 주입되어 있는 걸 확인할 수 있다.
        // callService class=class hello.springtx.apply.InternalCallV1Test$CallService$$EnhancerBySpringCGLIB$$b326fbb3
        log.info("callService class={}", callService.getClass());
    }

    @Test
    void internalCall() {
        callService.internal();
    }

    @Test
    void externalCall() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1Config {
        @Bean
        CallService callService() {
            return new CallService();
        }
    }

    @Slf4j
    static class CallService {
        public void external() {
            log.info("call external");
            printTxInfo();
            internal();

            // call external
            // tx active=false -> 여기서는 당연히 트랜잭션 적용 x
            // all internal -> 내부의 함수 호출
            // tx active=false -> 분명히 internal() 함수에는 트랜잭션이 붙어있지만 false로 나오는 걸 볼 수 있다.

            /*
                내부의 호출 흐름)
                1) 클라이언트가 callService.external()을 호출 -> 이때 callService는 트랜잭션 프록시이다.
                2) callService의 트랜잭션 프록시 호출
                3) external()에 트랜잭션이 안 붙어 있으니까 트랜잭션 적용 x -> 실제 callService의 external() 호출
                4) external()은 내부의 internal() 호출
                : 이는 별도의 참조가 없기 때문에, 자기 자신의 인스턴스를 호출해서 this.internal()이 되는데
                실제 대상 객체의 인스턴스가 된다. 즉, 프록시를 거치지 않기 때문에 트랜잭션을 적용하지 않는 것.

                ==> 그렇기 때문에, internal() 메서드를 별도의 클래스로 분리해야 한다.
             */

        }

        @Transactional
        public void internal() {
            log.info("call internal");
            // tx active=true
            // 여기서는 정상적으로 트랜잭션이 true이다.
            // (트랜잭션 프록시 callService가 @Transactional을 확인하고 트랜잭션 적용 후, internal() 호출한 다음 트랜잭션 완료)
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }
}
