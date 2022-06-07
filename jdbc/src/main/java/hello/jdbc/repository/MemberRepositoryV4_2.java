package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * SQLExceptionTranslator 추가
 */
@Slf4j
public class MemberRepositoryV4_2 implements MemberRepository {

    private final DataSource dataSource;
    private final SQLExceptionTranslator exTranslator;

    public MemberRepositoryV4_2(DataSource dataSource) {
        this.dataSource = dataSource;
        this.exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    // 저장
    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values (?, ?)";
        Connection conn = null;

        // statement의 자식타입으로, 파라미터 바인딩을 가능하게 해준다.
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            // 파라미터 바인딩 - 각각의 ? 값에 바인딩
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());

            // 실행 - SQL을 커넥션을 통해 실제 DB에 전달
            // 여기서 업데이트된 쿼리 수를 반환한다.
            pstmt.executeUpdate();
            return member;

        } catch (SQLException e) {
            // 스프링이 제공하는 예외로 변환하자!
            throw exTranslator.translate("save", sql, e);
        } finally {
            // 꼭 연결을 마지막에 닫아줘야 한다.
            // 만약 여기서 exception이 터진다면 connection이 안 닫힐 수도 있음
            // 이를 위해서 따로 메서드 빼주기
            close(conn, pstmt, null);
        }

    }

    /**************************/

    // 조회
    @Override
    public Member findById(String memberId) {

        // member_id, money라는 이름으로 데이터가 저장된다. (select 쿼리의 결과가 순서대로 들어감)
        String sql = "select * from member where member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        // resultSet의 내부에 있는 cursor를 이동해서 다음 데이터를 조회할 수 있다!
        ResultSet rs = null;
        /*
            ResultSet은 select 쿼리의 결과를 순서대로 저장하는 데이터 구조.
            select member_id, money라고 지정하면
            member_id, money라는 이름으로 데이터가 저장된다.

            커서 이동을 통해서 다음 데이터를 조회한다. (rs.next()로 이동)
         */


        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, memberId);

            // select 시에는 executeQuery.
            // select query의 결과를 담게 된다.
            rs = pstmt.executeQuery();

            // rs.next()부터 실제 결과가 담겨있다.
            if(rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
               // 결과 데이터를 다 받아왔으면
               throw new NoSuchElementException("member not found memberId = " + memberId);
            }

        } catch (SQLException e) {
            throw exTranslator.translate("findById", sql, e);
        } finally {
            close(conn, pstmt, rs);
        }
    }



    /******************************/

    // 수정
    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);

            // executeUpdate()는 쿼리를 실행하고 영향받은 row 수를 반환한다.
            int resultSize = pstmt.executeUpdate();

            // member_id(PK) 값으로 찝어오기 때문에 0이나 1이 나와야 한다.
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            throw exTranslator.translate("update", sql, e);
        } finally {
            close(conn, pstmt, null);
        }
    }


    /******************************/
    // 삭제
    @Override
    public void delete(String memberId) {
        String sql = "delete from member where member_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw exTranslator.translate("delete", sql, e);
        } finally {
            close(conn, pstmt, null);
        }

    }


    private void close(Connection conn, Statement stat, ResultSet rs) {
        // jdbcUtils를 사용하면 커넥션을 좀 더 편리하게 닫을 수 있다.
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stat);

        // 트랜잭션 동기화 사용 시 닫을 때도 이렇게 해줘야 함!
        // 동기화된 커넥션은 닫지 않고, 그대로 유지해줌!! (바로 닫는 게 아님!)
        // 만약 동기화 매니저가 관리하는 커넥션이 없을 때 커넥션을 닫는다.
        DataSourceUtils.releaseConnection(conn, dataSource);

    }

    private Connection getConnection() throws SQLException {
        // 트랜잭션 동기화를 위해 DataSourceUtils 사용하기!
        // 트랜잭션 동기화 매니저가 관리하는 커넥션이 있으면, 해당 커넥션을 반환한다!
        // 없으면 새로운 커넥션을 생성해서 반환한다.
        Connection conn = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}, class={}", conn, conn.getClass());
        return conn;
    }
}
