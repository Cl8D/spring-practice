package hello.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class TrafficController {

    /**
     * CPU 부하 테스트
     */
    @GetMapping("/cpu")
    public String cpu() {
        log.info("cpu");
        long value = 0;

        for (long i = 0; i < 100000000000L; i++) {
            value++;
        }

        return "ok value=" + value;
    }

    /**
     * JVM 메모리 부하 테스트
     */
    private List<String> list = new ArrayList<>();

    @GetMapping("/jvm")
    public String jvm() {
        log.info("jvm");
        for (int i = 0; i < 1000000; i++) {
            list.add("hello jvm!" + i);
        }
        return "ok";
    }

    /**
     * 커넥션 풀 고갈 테스트
     */

    @Autowired
    DataSource dataSource;
    @GetMapping("/jdbc")
    public String jdbc() throws SQLException {
        log.info("jdbc");
        Connection conn = dataSource.getConnection();
        log.info("connection info={}", conn);
        //conn.close();
        return "ok";
    }

    /**
     * 에러 레벨 로그 테스트
     */
    @GetMapping("/error-log")
    public String errorLog() {
        log.error("error log");
        return "error";
    }
}
