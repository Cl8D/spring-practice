package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {

    // DriverManager를 활용한 커넥션 획득
    @Test
    public void driverManager() throws Exception {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        // 로그를 확인하면 서로 다른 커넥션을 각각 가져오는 것을 볼 수 있다.
        // 15:08:57.740 [main] INFO hello.jdbc.connection.ConnectionTest - connection=conn0: url=jdbc:h2:tcp://localhost/~/jdbc user=SA, class=class org.h2.jdbc.JdbcConnection
        // 15:08:57.751 [main] INFO hello.jdbc.connection.ConnectionTest - connection=conn1: url=jdbc:h2:tcp://localhost/~/jdbc user=SA, class=class org.h2.jdbc.JdbcConnection
       log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }

    // 스프링이 제공하는 DriverManagerDataSource 사용하기
    @Test
    public void dataSourceDriverManager() throws Exception {
        // 항상 새로운 커넥션을 획득한다.
        // 참고로, 반환형으로 DataSource 형으로 받을 수 있다. (이미 스프링이 dataSource 인터페이스를 구현해놨기 때문에)
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        // 15:12:31.913 [main] DEBUG org.springframework.jdbc.datasource.DriverManagerDataSource - Creating new JDBC DriverManager Connection to [jdbc:h2:tcp://localhost/~/jdbc]
        // 15:12:32.220 [main] DEBUG org.springframework.jdbc.datasource.DriverManagerDataSource - Creating new JDBC DriverManager Connection to [jdbc:h2:tcp://localhost/~/jdbc]
        // 15:12:32.224 [main] INFO hello.jdbc.connection.ConnectionTest - connection=conn0: url=jdbc:h2:tcp://localhost/~/jdbc user=SA, class=class org.h2.jdbc.JdbcConnection
        // 15:12:32.227 [main] INFO hello.jdbc.connection.ConnectionTest - connection=conn1: url=jdbc:h2:tcp://localhost/~/jdbc user=SA, class=class org.h2.jdbc.JdbcConnection
        useDataSource(dataSource);
    }

    /*
        기존 DriverManager는 커넥션을 호출할 때마다 파라미터를 계속 전달해야 하지만,
        dataSource를 사용하는 방식에서는 처음 객체 생성 시에만 파라미터를 사용하고 <설정>
        사용할 때는 .getConnection()을 호출만 하면 된다. <사용>
        --> 즉, 리포지토리 차원에서도 dataSource에만 의존하고 세부 속성을 몰라도 되는 것.
     */

    // dataSource를 통해 커넥션 풀을 사용해보자.
    @Test
    public void dataSourceConnectionPool() throws Exception {
        // 히카리를 사용하자.
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        // 풀의 사이즈 지정
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);

        // 커넥션 풀에서 커넥션을 생성하는 작업은 애플리케이션과 별도의 스레드에서 작동하기 때문에
        // 테스트가 먼저 종료될 수 있어서 대기 시간을 줘야 커넥션 생성 로그를 볼 수 있다.
        Thread.sleep(1000);

        // 15:20:00.932 [MyPool connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - MyPool - Added connection conn2: url=jdbc:h2:tcp://localhost/~/jdbc user=SA
        // 여기서 별도의 스레드인 [MyPool Connection adder]를 사용하고 있는데,
        // 커넥션을 챙는 작업이 상대적으로 오래걸리기 때문에 애플리케이션 실행 시간을 늦어지지 않게 하기 위해 별도의 스레드를 사용한다.

        // 15:20:00.847 [main] INFO hello.jdbc.connection.ConnectionTest - connection=HikariProxyConnection@1237912220 wrapping conn0: url=jdbc:h2:tcp://localhost/~/jdbc user=SA, class=class com.zaxxer.hikari.pool.HikariProxyConnection
        // 15:20:00.848 [main] INFO hello.jdbc.connection.ConnectionTest - connection=HikariProxyConnection@757708014 wrapping conn1: url=jdbc:h2:tcp://localhost/~/jdbc user=SA, class=class com.zaxxer.hikari.pool.HikariProxyConnection
        // 커넥션을 보면 HikariProxyConnection을 사용하는 것을 볼 수 있다!

        //15:20:00.980 [MyPool connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - MyPool - After adding stats (total=10, active=2, idle=8, waiting=0)
        // 2개 사용하고 8개 대기 상태!
        // 만약 10개 이상을 호출하면 풀이 빌 때까지 기다리다가 일정 시간이 지나면 예외가 터진다.
    }


    private void useDataSource(DataSource dataSource) throws SQLException {
        // dataSource에서 커넥션 꺼내기
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }


}
