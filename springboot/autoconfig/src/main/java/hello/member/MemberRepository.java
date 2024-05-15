package hello.member;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final JdbcTemplate template;

    public void initTable() {
        template.execute("""
                    CREATE TABLE IF NOT EXISTS member (
                        member_id VARCHAR PRIMARY KEY, 
                        name VARCHAR
                    )
                """
        );
    }

    public void save(final Member member) {
        template.update("""
                            INSERT INTO member (member_id, name) 
                            VALUES (?, ?)
                        """,
                member.getMemberId(),
                member.getName()
        );
    }

    public Member find(String memberId) {
        return template.queryForObject("""
                            SELECT * FROM member 
                            WHERE member_id = ?
                        """,
                (rs, rowNum) -> new Member(
                        rs.getString("member_id"),
                        rs.getString("name")
                ),
                memberId
        );
    }

    public List<Member> findAll() {
        return template.query("""
                            SELECT member_id, name FROM member
                        """,
                (rs, rowNum) -> new Member(
                        rs.getString("member_id"),
                        rs.getString("name")
                )
        );
    }
}
