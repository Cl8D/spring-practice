package hello.springtx.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class RollbackTest {

    @Autowired
    RollbackService rollbackService;

    @Test
    void runtimeException() {
        assertThatThrownBy(() -> rollbackService.runtimeException())
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void checkedException() {
        assertThatThrownBy(() -> rollbackService.checkedException())
                .isInstanceOf(MyException.class);
    }

    @Test
    void rollbackFor() {
        assertThatThrownBy(() -> rollbackService.rollbackFor())
                .isInstanceOf(MyException.class);
    }


    @TestConfiguration
    static class RollbackTestConfig {
        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }

    @Slf4j
    static class RollbackService {
        // 런타임 예외 발생: 롤백
        @Transactional
        public void runtimeException() {
            log.info("call runtimeException");
            throw new RuntimeException();
            /*
              Getting transaction for [hello.springtx.exception.RollbackTest$RollbackService.runtimeException]
              - 트랜잭션 얻어오기 (@Transactional)
              call runtimeException - 호출!
              Completing transaction for [hello.springtx.exception.RollbackTest$RollbackService.runtimeException] after exception: java.lang.RuntimeException
              -> 런타임 예외 발생!

              Initiating transaction rollback - 롤백 시작
              Rolling back JPA transaction on EntityManager [SessionImpl(820069375<open>)] - 롤백 완료
             */
        }

        // 체크 예외 발생 : 커밋
        @Transactional
        public void checkedException() throws MyException {
            log.info("call checkedException");
            throw new MyException();
            /*
                Getting transaction for [hello.springtx.exception.RollbackTest$RollbackService.checkedException]
                call checkedException - 호출
                Completing transaction for [hello.springtx.exception.RollbackTest$RollbackService.checkedException] after exception: hello.springtx.exception.RollbackTest$MyException
                -> 체크 예외 발생!

                Initiating transaction commit - 커밋 시작
                Committing JPA transaction on EntityManager [SessionImpl(1091523506<open>)] - 커밋 완료
             */
        }

        // 체크 예외지만 rollbackFor 지정 : 롤백
        @Transactional(rollbackFor = MyException.class)
        public void rollbackFor() throws MyException {
            log.info("call rollbackFor");
            throw new MyException();
            /*
                Getting transaction for [hello.springtx.exception.RollbackTest$RollbackService.rollbackFor]
                call rollbackFor - 호출
                Completing transaction for [hello.springtx.exception.RollbackTest$RollbackService.rollbackFor] after exception: hello.springtx.exception.RollbackTest$MyException
                - 체크 예외 발생!

                Initiating transaction rollback - 롤백 시작
                Rolling back JPA transaction on EntityManager [SessionImpl(1101590744<open>)] - 롤백 완료
             */
        }
    }

    static class MyException extends Exception {
    }

}
