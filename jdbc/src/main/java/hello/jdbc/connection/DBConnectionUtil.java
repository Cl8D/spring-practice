package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {
    public static Connection getConnection() {
        try {
            // jdbc가 제공하는 DriverManger.getConnection()을 사용하여 DB에 연결하자.
            // 가져온 connection의 구현체가 각각의 db에 맞는 드라이버를 가져오게 된다.
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            // log 확인 결과, h2의 경우 orgs.h2.jdbc.jdbcConnection을 가져오게 된다.
            // 15:05:22.986 [main] INFO hello.jdbc.connection.DBConnectionUtil - get connection=conn0: url=jdbc:h2:tcp://localhost/~/jdbc user=SA,
            // class=class org.h2.jdbc.JdbcConnection
            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
