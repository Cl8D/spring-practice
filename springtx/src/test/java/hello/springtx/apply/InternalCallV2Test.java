package hello.springtx.apply;

import lombok.RequiredArgsConstructor;
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
public class InternalCallV2Test {

    @Autowired
    CallService callService;

    @Test
    void externalCallV2() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV2Config {
        @Bean
        CallService helloService() {
            return new CallService(innerService());
        }

        @Bean
        InternalService innerService() {
            return new InternalService();
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    static class CallService {
        private final InternalService internalService;

        public void external() {
            log.info("call external");
            printTxInfo();
            // 메서드 내부 호출에서 외부 호출로 변경
            // callSerivce는 트랜잭션이 없으니까 프록시 저용 x
            // internalService에는 트랜잭션이 있으니까 프록시 적용 o
            internalService.internal();

            /*
                실행 흐름)
                1) 클라이언트가 callService.external() 호출
                2) 얘는 실제 callService의 객체 인스턴스가 된다.
                3) callService는 주입받은 internalService.internal() 호출.
                4) 트랜잭션 프록시이며, @Transactional이 붙어 있으니까 트랜잭션 적용
                5) 적용 후, 실제 internalService 객체 인스턴스의 internal()을 호출한다.
             */
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }


    @Slf4j
    static class InternalService {
        @Transactional
        public void internal() {
            log.info("call internal");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }

    /*
        추가적으로, @Transactional은 public 메서드에만 적용이 가능하다.
        (예외가 발생하지는 않고, 그냥 무시됨)
        -> 왜냐면 의도하지 않은 곳까지 트랜잭션이 과도하게 적용될 수 있으니까.
     */
}
