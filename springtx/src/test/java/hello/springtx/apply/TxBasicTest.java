package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class TxBasicTest {

    @Autowired
    BasicService basicService; // 여기서 프록시가 스프링 빈에 있기 때문에, 주입도 프록시가 된다.

    @Test
    void proxyCheck() {
        // aop class=class hello.springtx.apply.TxBasicTest$BasicService$$EnhancerBySpringCGLIB$$af334ba5
        // 보면 basicService가 프록시 객체인 걸 확인할 수 있다. (SpringCGLIB)
        log.info("aop class={}", basicService.getClass());

        // 스프링 트랜잭션은 AOP 기반으로 동작함.
        // @Transactional이 붙어있는 애는 트랜잭션 AOP의 적용 대상이고,
        // 이러면 실제 객체 대신에 트랜잭션을 처리해주는 프록시 객체가 스프링 컨테이너 - 스프링 빈에 등록된다.
        // 주입 시에도 프록시 객체가 주입됨
        // 프록시 객체는 실제 basicService를 참조하고 있다.
        Assertions.assertThat(AopUtils.isAopProxy(basicService)).isTrue();
    }

    @Test
    void txTest() {
        // 여기서 프록시의 tx()가 호출된다. -> tx()는 @Transactional로 인해 트랜잭션 적용 대상
        // 그러면 트랜잭션을 시작한 다음에, 실제 basicService.tx()를 호출해준다.
        // 함수 종료 후 제어가 프록시로 돌아오면, 프록시가 트랜잭션 로직을 커밋하거나 롤백 후 종료해준다.
        basicService.tx();
        // 여기는 트랜잭션이 안 붙어 있으니까 그냥 실제 basicService.nonTx() 호출 후 종료한다.
        basicService.nonTx();
    }

    @TestConfiguration
    static class TxApplyBasicConfig {
        @Bean
        public BasicService basicService() {
            return new BasicService();
        }
    }


    @Slf4j
    static class BasicService {

        @Transactional
        public void tx() {
            log.info("call tx");
            // 트랜잭션 적용 여부 확인
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            // tx active=true
            log.info("tx active={}", txActive);
        }

        public void nonTx() {
            log.info("call nonTx");
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            // tx active=false
            log.info("tx active={}", txActive);
        }

    }

}
