package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Exception - 언체크 예외
 */
@Slf4j
public class UncheckedTest {

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

    // RuntimeException을 상속받은 예외 = 언체크 예외
    static class MyCheckedException extends RuntimeException {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    // unchecked 예외는 예외 잡거나 던지지 않아도 된다!
    // 예외 안 잡으면 알아서 밖으로 던져줌!
    static class Service {
        Repository repository = new Repository();

        // 필요할 때 예외를 잡아서 처리해주면 된다.
        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }

        // throws로 선언을 안 해줘도, 자동으로 상위로 올라간다.
        // 물론 선언해도 되는데, 선언하면 개발자가 알아보기 조금 더 쉽다는 것 정도...?
        public void callThrow() {
            repository.call();
        }

    }

    static class Repository {
        public void call()  {
            throw new MyCheckedException("ex");
        }
    }

}
