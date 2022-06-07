package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JdbcTemplate 사용하기
 * -> JDBC 사용으로 생기는 반복 문제 해결하기.
 * 커넥션 조회, 동기화
 * PreparedStatement 생성 및 파라미터 바인딩
 * 쿼리 실행
 * 결과 바인딩
 * 예외 발생 시 스프링 예외 변환기 실행
 * 리소스 종료
 *
 * --> '템플릿 콜백 패턴' 사용하기. 여기서 JdbcTemplate이 바로 템플릿이 된다!
 */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository {

    private final JdbcTemplate template;

    public MemberRepositoryV5(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    // 저장
    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values (?, ?)";
        // template에서 이런 걸 다 해준다...!
        template.update(sql, member.getMemberId(), member.getMoney());
        Connection conn = null;
        return member;
    }

    /**************************/

    // 조회
    @Override
    public Member findById(String memberId) {
        // member_id, money라는 이름으로 데이터가 저장된다. (select 쿼리의 결과가 순서대로 들어감)
        String sql = "select * from member where member_id = ?";
        // 단건조회 시 queryForObject 사용
        return template.queryForObject(sql, memberRowMapper(), memberId);
    }



    /******************************/

    // 수정
    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";
        template.update(sql, money, memberId);
    }


    /******************************/
    // 삭제
    @Override
    public void delete(String memberId) {
        String sql = "delete from member where member_id=?";
        template.update(sql, memberId);
    }


    // 조회할 때는 mapping 과정 필요!
    private RowMapper<Member> memberRowMapper() {
        // resultSet과 row number.
        return(rs, rowNum) -> {
             Member member = new Member();
             member.setMemberId(rs.getString("member_id"));
             member.setMoney(rs.getInt("money"));
             return member;
        };
    }
}
