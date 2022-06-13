package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager txManager;

    @TestConfiguration
    static class Config {
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }
    @Test
    void commit() {
        log.info("트랜잭션 시작");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션 커밋 시작");
        txManager.commit(status);
        log.info("트랜잭션 커밋 완료");
        /*
            <트랜잭션 시작>
            - 커넥션 가져오기
            Acquired Connection [HikariProxyConnection@1587485260 wrapping conn0: url=jdbc:h2:mem:7d8761de-dc6b-4862-8abb-6eea92795fd3 user=SA] for JDBC transaction
            - JDBC 커넥션 사용
            Switching JDBC Connection [HikariProxyConnection@1587485260 wrapping conn0: url=jdbc:h2:mem:7d8761de-dc6b-4862-8abb-6eea927
            <트랜잭션 커밋 시작>
            ...
            - 커밋하기
            Committing JDBC transaction on Connection [HikariProxyConnection@1587485260 wrapping conn0
            - 커넥션 되돌려주기
            Releasing JDBC Connection [HikariProxyConnection@1587485260 wrapping conn0
            <트랜잭션 커밋 완료>
         */
    }

    @Test
    void rollback() {
        log.info("트랜잭션 시작");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션 롤백 시작");
        txManager.rollback(status);
        log.info("트랜잭션 롤백 완료");
        /*
            <트랜잭션 시작>
            - 과정 똑같음
            <트랜잭션 롤백 시작>
            Initiating transaction rollback
            Rolling back JDBC transaction on Connection
            Releasing JDBC Connection
            <트랜잭션 롤백 완료>
         */
    }

    @Test
    void double_commit() {
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋");
        txManager.commit(tx1);

        /*
            <트랜잭션1 시작>
            Switching JDBC Connection [HikariProxyConnection@130574494 wrapping conn0 ==> conn0 커넥션 사용
            <트랜잭션1 커밋>
            Committing JDBC transaction on Connection [HikariProxyConnection@130574494 wrapping conn0
            Releasing JDBC Connection [HikariProxyConnection@130574494 wrapping conn0
            ==> conn0을 커밋 후 반납
         */

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 커밋");
        txManager.commit(tx2);

        /*
            <트랜잭션2 시작>
            Switching JDBC Connection [HikariProxyConnection@767266208 wrapping conn0 ==> conn0 커넥션 사용
            <트랜잭션2 커밋>
            Committing JDBC transaction on Connection [HikariProxyConnection@767266208 wrapping conn0
            Releasing JDBC Connection [HikariProxyConnection@767266208 wrapping conn0
            ==> conn0을 커밋 후 반납
         */

        // 둘 다 같은 conn0을 사용한 것을 볼 수 있다. = 커넥션 풀 때문! = 그래서 사실상 완전히 다른 커넥션인 게 맞다.
        // 히카리 커넥션 풀에서 커넥션을 획득하면, 실제 커넥션을 그대로 반환하는 것이 아닌 내부 관리를 위해
        // 히카리 프록시 커넥션(HikariProxyConnection)이라는 객체를 생성하여 반환한다. (내부에 실제 커넥션 포함)
        // 해당 객체의 주소를 확인하면, 커넥션 풀에서 획득한 커넥션을 구분 가능하다.

        // 위 예제에서도 보면, 트랜잭션1의 경우 HikariProxyConnection@130574494, 2의 경우 HikariProxyConnection@767266208을 사용해서 서로 다르다.
        // conn0을 통해 커넥션이 재사용된 것이고, 각각 히카리 프록시 커넥션을 통해 커넥션 풀에서 커넥션을 조회한 것이다.
        // 즉, 재사용이 아니라, 반납하고 다시 새로 받아서 쓴 거임!
        // 풀이 없으면 그냥 conn0, conn1였을 거임.
    }

    @Test
    void double_commit_rollback() {
        // 각자 동작하기 때문에 트랜잭션1은 커밋, 트랜잭션2는 롤백된다.

        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋");
        txManager.commit(tx1);
        /*
            Committing JDBC transaction on Connection [HikariProxyConnection@550917991 wrapping conn0
            Releasing JDBC Connection [HikariProxyConnection@550917991 wrapping conn0
         */

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 롤백");
        txManager.rollback(tx2);
        /*
            Rolling back JDBC transaction on Connection [HikariProxyConnection@1705173326 wrapping conn0
            Releasing JDBC Connection [HikariProxyConnection@1705173326 wrapping conn0
         */
    }

    @Test
    void inner_commit() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        // Switching JDBC Connection [HikariProxyConnection@1463856502 wrapping conn0
        // outer.isNewTransaction()=true ==> 새로운 트랜잭션
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        // Participating in existing transaction
        // inner.isNewTransaction()=false => 기존에 있던 트랜잭션에 참여
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());

        log.info("내부 트랜잭션 커밋");
        txManager.commit(inner);

        log.info("외부 트랜잭션 커밋");
        txManager.commit(outer);
        /*
            2022-06-13 09:42:21.407  INFO 6152 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 내부 트랜잭션 커밋
            2022-06-13 09:42:21.408  INFO 6152 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 외부 트랜잭션 커밋
            Initiating transaction commit
            Committing JDBC transaction on Connection [HikariProxyConnection@1463856502 wrapping conn0
            Releasing JDBC Connection [HikariProxyConnection@1463856502 wrapping conn0

            ==> 외부 트랜잭션만 물리 트랜잭션을 시작하고 커밋한다.
            (내부가 커밋해버리면 트랜잭션이 종료되니까, 처음 트랜잭션을 시작한 외부 트랜잭션이 진행하는 것)
         */


        /*
            외부 트랜잭션이 수행 중일 때, 내부 트랜잭션을 추가로 수행한 경우
            -> 내부가 외부 트랜잭션에 참여하면서, 하나의 물리 트랜잭션으로 묶임
         */
    }

    @Test
    void outer_rollback() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("내부 트랜잭션 시작");
        // Participating in existing transaction = 마찬가지로 이미 존재하는 트랜잭션에 참여
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트랜잭션 커밋");
        txManager.commit(inner);
        log.info("외부 트랜잭션 롤백");
        txManager.rollback(outer);

        /*
            2022-06-13 10:02:11.380  INFO 22016 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 내부 트랜잭션 커밋
            2022-06-13 10:02:11.383  INFO 22016 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 외부 트랜잭션 롤백

            Initiating transaction rollback
            Rolling back JDBC transaction on Connection
            Releasing JDBC Connection

            : 내부 트랜잭션은 물리 트랜잭션에 관여를 안 한다.
            : 그렇기 때문에, 외부 트랜잭션이 롤백되면서 전체 트랜잭션의 모든 내용이 함께 롤백됨!
            = 외부의 범위가 내부까지 사용됨
         */
    }

    @Test
    void inner_rollback() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트랜잭션 롤백");
        txManager.rollback(inner);
        log.info("외부 트랜잭션 커밋");
        assertThatThrownBy(() -> txManager.commit(outer)).isInstanceOf(UnexpectedRollbackException.class);
        /*
            2022-06-13 10:08:49.186  INFO 19284 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 내부 트랜잭션 롤백
            Participating transaction failed - marking existing transaction as rollback-only
            -> 실제 물리 트랜잭션은 롤백하지 않지만, 기존 트랜잭션을 롤백 전용으로 표시해준다.
            Setting JDBC transaction [HikariProxyConnection@1463856502 wrapping conn0

            2022-06-13 10:08:49.190  INFO 19284 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 외부 트랜잭션 커밋
            Global transaction is marked as rollback-only but transactional code requested commit
            -> 커밋을 호출했지만 롤백 전용으로 표시되어 있기 때문에 물리 트랜잭션을 롤백한다.
            Initiating transaction rollback
            Rolling back JDBC transaction on Connection [HikariProxyConnection@1463856502 wrapping conn0
            Releasing JDBC Connection [HikariProxyConnection@1463856502 wrapping conn0
         */
    }

    @Test
    void inner_rollback_requires_new() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        // outer.isNewTransaction()=true
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
        // 외부와 내부에 별도의 물리 트랜잭션을 부여하기 위해 PROPAGATION_REQUIRES_NEW 옵션 지정
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        /*
            Suspending current transaction, creating new transaction with name [null]
            Acquired Connection [HikariProxyConnection@825281081 wrapping conn1
            => 새로운 conn1을 받아온 걸 볼 수 있다!
         */
        TransactionStatus inner = txManager.getTransaction(definition);
        // inner.isNewTransaction()=true
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());

        log.info("내부 트랜잭션 롤백");
        txManager.rollback(inner);

        log.info("외부 트랜잭션 커밋");
        txManager.commit(outer);

        /*
            2022-06-13 10:28:18.305  INFO 20728 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 내부 트랜잭션 롤백
            Initiating transaction rollback
            Rolling back JDBC transaction on Connection
            Releasing JDBC Connection
            Resuming suspended transaction after completion of inner transaction

            2022-06-13 10:28:18.307  INFO 20728 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 외부 트랜잭션 커밋
            Initiating transaction commit
            Committing JDBC transaction on Connection
            Releasing JDBC Connection

            -> 둘 다 신규 트랜잭션이기 때문에 내부 트랜잭션은 conn1에 물리 롤백을, 외부 트랜잭션은 conn0에 물리 커밋을 진행한다.
         */
    }


}
