package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

/**
 * Exception - 언체크 예외 활용하기
 */
@Slf4j
public class UnCheckedAppTest {

    /*
        예외 처리 못하면 딱히 선언 안 해도 된다!
     */
    @Test
    void unchecked() {
        Controller controller = new Controller();
        // callThrow는 예외를 처리하지 않고 밖으로 던지니까 여기까지 올라온다.
        // 여기서 asserThat으로 instance 체크한 것.
        Assertions.assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class);
    }

    @Test
    void printEx() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
            // 이전에 기존 예외를 넘겨줬으니까 기존 예외가 무엇인지 확인이 가능함.
            // 01:36:07.500 [main] INFO hello.jdbc.exception.basic.UnCheckedAppTest - ex
            // hello.jdbc.exception.basic.UnCheckedAppTest$RuntimeSQLException: java.sql.SQLException: ex

            // ...
            // Caused by: java.sql.SQLException: ex
            //	at hello.jdbc.exception.basic.UnCheckedAppTest$Repository.runSQL(UnCheckedAppTest.java:79)
            //	at hello.jdbc.exception.basic.UnCheckedAppTest$Repository.call(UnCheckedAppTest.java:70)

            log.info("ex", e);

            // 기존 예외를 안 넘겨주면 출력했을 때 - 기존의 예외가 안 나오고 변환된 애에 대해서만 스택 트레이스를 볼 수 있다...
            // 실제 DB와 연동했을 때 DB에서 발생한 예외를 확인할 수 없는 문제가 생긴다...
            // [Test worker] INFO hello.jdbc.exception.basic.UncheckedAppTest - ex
            //hello.jdbc.exception.basic.UncheckedAppTest$RuntimeSQLException: null

        }
    }

    static class Controller {
        Service service = new Service();

        // throws를 해줄 필요가 없다. 쓸모없는 의존관계가 생기지 않는다!
        public void request() {
            service.logic();
        }

    }

     static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();


        public void logic()  {
            repository.call();
            networkClient.call();
        }

    }

    static class NetworkClient {
        public void call() {
            // 여기서는 기존 체크 예외 대신에 런타임 예외로 바꿔줌
            throw new RuntimeConnectException("연결 실패");
        }
    }

    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                // 여기서 체크 예외가 발생하면 런타임 예외인 RuntimeSqLException으로 전환해서 예외를 던져준다.
                // 기존 예외 (e)를 포함해야 예외 출력할 때 같이 확인할 수 있음.
                throw new RuntimeSQLException(e);
            }
        }
        // 체크 예외인 SQLException
        private void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    // 언체크 예외 - runtimeException
    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException() {
        }

        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}
