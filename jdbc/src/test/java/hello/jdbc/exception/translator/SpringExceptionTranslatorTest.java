package hello.jdbc.exception.translator;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class SpringExceptionTranslatorTest {
    DataSource dataSource;

    @BeforeEach
    void init() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }

    @Test
    void sqlExceptionErrorCode() {
        // 문법이 잘못된 SQL 예시
        String sql = "select bad grammar";
        try {
            Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.executeQuery();

        } catch (SQLException e) {
            // h2의 경우 문법 오류의 경우 errorCode가 42122이다.
            // 이런 식으로 에러 코드를 직접 확인해서 스프링이 만든 예외로 바꾸는 건 너무 힘들다...
            Assertions.assertThat(e.getErrorCode()).isEqualTo(42122);
//            int errorCode = e.getErrorCode();
//            // [main] INFO hello.jdbc.exception.translator.SpringExceptionTranslatorTest - errorCode=42122
//            log.info("errorCode={}", errorCode);
//            // [main] INFO hello.jdbc.exception.translator.SpringExceptionTranslatorTest - error
//            log.info("error", e);


            // 스프링이 제공하는 예외 변환기 사용하기
            SQLErrorCodeSQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);

            // 실행한 sql과 발생된 Exception을 넘겨주면, 스프링 데이터 접근 계층의 예외로 변환해준다.
            // 여기서는 SQL 문법 오류이기 때문에 BadSqlGrammarException을 반환해준다. (얘의 부모가 DataAccessException이라 리턴을 저렇게 받아도 된당)
            DataAccessException resultEx = exTranslator.translate("select", sql, e);

            // 23:47:42.110 [main] INFO hello.jdbc.exception.translator.SpringExceptionTranslatorTest - resultEx
            //org.springframework.jdbc.BadSqlGrammarException: select; bad SQL grammar [select bad grammar]; nested exception is org.h2.jdbc.JdbcSQLSyntaxErrorException: Column "BAD" not found; SQL statement:
            log.info("resultEx", resultEx);

            Assertions.assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);
        }

        /*
            스프링은 예외 변환기를 통해서 SQLException의 ErrorCode에 맞는 적절한 스프링 데이터 접근 예외로 바꿔준다.
            -> 서비스, 컨트롤러 계층에서 예외 처리가 필요하면 스프링이 제공하는 데이터 접근 예외를 사용해주면 된다!
            --> 덕분에 특정 기술에 종속적이지 않으며, 예외로 인한 변경을 최소화할 수 있다.
         */
    }

}
