package hello.jdbc.exception.translator;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import hello.jdbc.repository.ex.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.springframework.jdbc.support.JdbcUtils.closeConnection;
import static org.springframework.jdbc.support.JdbcUtils.closeStatement;

public class ExTranslatorV1Test {

    Repository repository;
    Service service;

    @BeforeEach
    void init() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,
                USERNAME, PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);
    }

    @Test
    void duplicateKeySave() {
        service.create("myId");
        service.create("myId");//같은 ID 저장 시도
    }

    @Slf4j
    @RequiredArgsConstructor
    static class Service {
        private final Repository repository;

        public void create(String memberId) {
            try {
                repository.save(new Member(memberId, 0));
                // 처음에 저장한 거는 성공!
                // INFO hello.jdbc.exception.translator.ExTranslatorV1Test$Service - saveId=myId
                log.info("saveId={}", memberId);

            } catch (MyDuplicateKeyException e) {
                // 리포지토리에서 키 중복에 관한 예외가 올라오면 잡아준다.
                log.info("키 중복, 복구 시도");

                // 예외를 받았으니까 새로운 ID를 생성하도록 시도해준다. (예외를 복구하는 부분)
                String retryId = generateNewId(memberId);
                // INFO hello.jdbc.exception.translator.ExTranslatorV1Test$Service - retryId=myId8849
                log.info("retryId={}", retryId);

                // 그리고 다시 저장!
                repository.save(new Member(retryId, 0));
            } catch (MyDbException e) {
                // 복구 불가능한 예외면 그냥 던지기
                // 여기서 로그를 남기는 것보다는, 던져져서 공통으로 처리되는 곳에서 남겨주는 게 좋다!
                log.info("데이터 접근 계층 예외", e);
                throw e;
            }
        }
        private String generateNewId(String memberId) {
            return memberId + new Random().nextInt(10000);
        }
    }


    @RequiredArgsConstructor
    static class Repository {
        private final DataSource dataSource;

        public Member save(Member member) {
            // db에 그냥 insert 해주는 단순한 코드
            String sql = "insert into member(member_id, money) values(?, ?)";

            Connection con = null;
            PreparedStatement pstmt = null;

            try {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement(sql);

                pstmt.setString(1, member.getMemberId());
                pstmt.setInt(2, member.getMoney());

                pstmt.executeUpdate();

                return member;
            } catch (SQLException e) {
                //h2 database 기준, 23505 errorCode는 키 중복 오류.
                // 그러나, 이 역시 db가 바뀌면 errorCode도 바뀌니깐 또 처리해줘야 함...
                // 이런 건 스프링에서 처리해준다!
               if (e.getErrorCode() == 23505) {
                   // 서비스 계층에 던져주기!
                    throw new MyDuplicateKeyException(e);
                }
               // 키 중복이 아니라면 그냥 런타임예외로 바꿔주기
                throw new MyDbException(e);
            } finally {
                closeStatement(pstmt);
                closeConnection(con);
            }
        }
    }
}
