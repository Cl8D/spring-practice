package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;

// <Entity-Type, PK> 형식
public interface MemberRepository extends JpaRepository<Member, Long> {
    /*
        이런 식으로 인터페이스만 있어도, springJPA가 알아서 구현 클래스를 만들어준다.
        (프록시 객체로 만들어버림)

        - 또한, Repository 애노테이션이 생략 가능하다.
        -> 컴포넌트 스캔을 spring data jpa가 알아서 해주며,
        jpa 예외를 스프링 예외로 변환하는 것까지 자동으로 처리해준다.
     */


    // 이런 식으로 가운데에 식별자 써도 됨
    List<Member> findTop3HelloBy();

    // 이런 식으로 여러 조건을 걸 수 있지만, 메서드 이름이 너무 길다.
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 여러 조건을 걸 때는 이런 식으로 메서드에 바로 쿼리를 적어주는 게 낫다!
    @Query("select m from Member m where m.username= :username and m.age= :age")
    List<Member> findUser(@Param("username") String username,
                          @Param("age") int age);


    // JPA의 값 타입 (@Embedded 방식도 이런 식으로 조회 가능함)
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // 이런 식으로 dto를 조회할 수도 있다.
    // DTO에 member-team이 존재하니까 둘을 join해서 가져와야 함
    // 단, DTO는 실제 엔티티가 아니기 때문에 new 명령어를 사용해야 한다. (+생성자도 필요)
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name)"
            + " from Member m join m.team t")
    List<MemberDto> findMemberDto();

}

