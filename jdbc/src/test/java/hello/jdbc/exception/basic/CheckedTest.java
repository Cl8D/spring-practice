package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Exception - 체크 예외
 */
@Slf4j
public class CheckedTest {

    @Test
    void checked_catch() {
        Service service = new Service();
        service.callCatch(); // 잡아서 처리하기 - 처리했으니깐 메서드단까지 올라오지 않음!
        // INFO hello.jdbc.exception.basic.CheckedTest - 예외 처리, message=ex
        // 사실 밑에 예외에 대한 stackTrace가 추가적으로 출력되는데, 이는 log.info에서 세 번째 인자로 e를 넘겨줘서 그럼
    }

    @Test
    void checked_throw() {
        Service service = new Service();
        // callThrow는 예외를 처리하지 않고 밖으로 던지니까 여기까지 올라온다.
        // 여기서 asserThat으로 instance 체크한 것.
        Assertions.assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyCheckedException.class);


    }

    // Exception을 상속받은 예외 = 체크 예외
    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    // checked 예외는 예외를 잡아서 처리하거나, 던지거나 둘 중 하나를 필수로 해야 한다!
    static class Service {
        Repository repository = new Repository();

        // 예외를 잡아서 처리하는 코드
        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }

        // 예외를 밖으로 던지는 코드
        // throws를 반드시 선언해야 한다. - 아니면 컴파일 오류
        public void callThrow() throws MyCheckedException {
            repository.call();
        }

    }

    static class Repository {
        // 여기서 예외를 잡아서 처리하지 않을 거니깐 밖으로 던지기
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }

}
