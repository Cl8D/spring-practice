package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

// JDBC - DriverManager 사용
@Slf4j
public class MemberRepositoryV0 {

    // 저장
    public Member save(Member member) throws SQLException {
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
            log.error("db error", e);
            throw e;
        } finally {
            // 꼭 연결을 마지막에 닫아줘야 한다.
            // 만약 여기서 exception이 터진다면 connection이 안 닫힐 수도 있음
            // 이를 위해서 따로 메서드 빼주기
            close(conn, pstmt, null);
        }

    }

    /**************************/

    // 조회
    public Member findById(String memberId) throws SQLException {

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
           log.error("error", e);
           throw e;
        } finally {
            close(conn, pstmt, rs);
        }
    }

    /******************************/

    // 수정
    public void update(String memberId, int money) throws SQLException {
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
            log.error("db error", e);
            throw e;
        } finally {
            close(conn, pstmt, null);
        }
    }

    /******************************/
    // 삭제
    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(conn, pstmt, null);
        }

    }


    private void close(Connection conn, Statement stat, ResultSet rs) {
        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }


        if(stat != null) {
            try {
                stat.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }

        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }

    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
