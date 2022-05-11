package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repositoryV1;

    // 각 테스트가 실행되기 전에 먼저 실행
    @BeforeEach
    void beforeEach() {
        // 기본 driverManager를 통해 새로운 커넥션 획득하기.
//        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        // 그리고 주입! (requiredArgConstructor)

        // 커넥션 풀 사용하기
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repositoryV1 = new MemberRepositoryV1(dataSource);
    }

    @Test
    public void crud() throws Exception {
        // save test
        Member member = new Member("memberV100", 10000);
        repositoryV1.save(member);

        // findById test
        Member findMember = repositoryV1.findById(member.getMemberId());
        // 16:20:43.799 [main] INFO hello.jdbc.repository.MemberRepositoryV0Test - findMember=Member(memberId=memberV0, money=10000)
        // 참고로, lombok의 toString 때문에 이런 식으로 나온 것임!
        log.info("findMember={}", findMember);

        // cf) member==findMember => false
        // member.equals(findMember) => true -> lombok 내부의 equals를 사용하여 비교하기 때문에
        // Data annotation 사용시 equals, hashcode 모두 지원
        // 마찬가지로 isEqualTo도 equals를 써서 비교하기 때문에 동일한 객체 되는 것
        assertThat(findMember).isEqualTo(member);

        // update test: money(10000->20000)
        repositoryV1.update(member.getMemberId(), 20000);
        Member updatedMember = repositoryV1.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        // delete test
        repositoryV1.delete(member.getMemberId());
        // exception을 이용해서 tdd 짜기!
        Assertions.assertThatThrownBy(() ->
                repositoryV1.findById(member.getMemberId()))
                        .isInstanceOf(NoSuchElementException.class);

        Thread.sleep(1000);
    }
    /*
        1번 테스트 결과)
        15:39:53.556 [main] INFO hello.jdbc.repository.MemberRepositoryV1 - get connection=conn5: url=jdbc:h2:tcp://localhost/~/jdbc user=SA, class=class org.h2.jdbc.JdbcConnection
        쿼리를 하나 실행할 때마다 계속 새로운 커넥션을 맺어서 conn5까지 생성된 것을 확인할 수 있다.
        -> 성능이 느리다. 그래서 커넥션 풀을 사용하고자 하는 것!

        2번 테스트 결과)
        15:43:30.678 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection conn9: url=jdbc:h2:tcp://localhost/~/jdbc user=SA
        -> conn0~conn9까지 풀을 먼저 채운 다음에,
        15:43:30.770 [main] INFO hello.jdbc.repository.MemberRepositoryV1 - get connection=HikariProxyConnection@1332691311 wrapping conn0: url=jdbc:h2:tcp://localhost/~/jdbc user=SA, class=class com.zaxxer.hikari.pool.HikariProxyConnection
        -> 보면 다 wrapping으로 conn0이 되어있는 걸 볼 수 있다.
        이는, 코드 수행 후 close() 함수에서 JdbcUtils.closeConnection()을 하게 되면
        커넥션을 닫는 게 아니라 그냥 커넥션 풀에 반환한다.

        그렇기 때문에 쓰고 반환 - 쓰고 반환... 이러니까 계속 가장 위에 있는 conn0을 사용하게 되는 것!

     */

}