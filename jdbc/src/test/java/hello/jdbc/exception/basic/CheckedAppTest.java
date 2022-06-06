package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

/**
 * Exception - 체크 예외 활용하기
 */
@Slf4j
public class CheckedAppTest {

    /*
        복구 불가능한 예외여서 그냥 계속 던짐...
        계속 throws를 통해서 예외를 던져줘야 한다는 단점이 있다.
        -> 이러면 서비스, 컨트롤러 단에서 SQLException을 의존하니깐 안 좋은 것임!
        ==> 나중에 JDBC가 아니라 다른 exception으로 예외가 바뀌면 JPAException에 의존해야 하니깐 힘들어진다.
        ---> 즉, 불필요한 의존관계가 발생하게 되는 것.

        -> 그러면, throws로 최상위 예외인 Exception을 던지면 안 될까?
        : 이러면 모든 체크 예외를 밖으로 다 던지니까, 중요한 체크 예외를 다 놓치게 된다.
        : 특히, 컴파일단에서 오류가 안 발생하니깐 좋지 않은 방법임!...

        (보통은 ControllerAdvice를 사용해서 처리를 해준다)
     */
    @Test
    void checked() {
        Controller controller = new Controller();
        // callThrow는 예외를 처리하지 않고 밖으로 던지니까 여기까지 올라온다.
        // 여기서 asserThat으로 instance 체크한 것.
        Assertions.assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class);
    }



    static class Controller {
        Service service = new Service();
        // 컨트롤러에서도 처리못해서 던짐
        public void request() throws SQLException, ConnectException {
            service.logic();
        }

    }
    // checked 예외는 예외를 잡아서 처리하거나, 던지거나 둘 중 하나를 필수로 해야 한다!
    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        // 서비스에서 처리 못하니깐 컨트롤러로 던짐
        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }

    }

    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("연결 실패!");
        }
    }

    static class Repository {
        // 여기서 예외를 잡아서 처리하지 않을 거니깐 밖으로 던지기
        public void call() throws SQLException {
            throw new SQLException("ex");
        }
    }

}
