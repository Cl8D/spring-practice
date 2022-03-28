package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;

// <Entity-Type, PK> 형식
// 우리가 만든 memberRepositoryCustom도 상속해준다.
// 이러면 실제 사용할 때 memberRepository.findMemberCustom()처럼 사용 가능!
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    /*
        이런 식으로 인터페이스만 있어도, springJPA가 알아서 구현 클래스를 만들어준다.
        (프록시 객체로 만들어버림)

        - 또한, Repository 애노테이션이 생략 가능하다.
        -> 컴포넌트 스캔을 spring data jpa가 알아서 해주며,
        jpa 예외를 스프링 예외로 변환하는 것까지 자동으로 처리해준다.
     */


    /*******************************/

    List<Member> findByUsername(String name);

    // 이런 식으로 가운데에 식별자 써도 됨
    List<Member> findTop3HelloBy();

    // 이런 식으로 여러 조건을 걸 수 있지만, 메서드 이름이 너무 길다.
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 여러 조건을 걸 때는 이런 식으로 메서드에 바로 쿼리를 적어주는 게 낫다!
    @Query("select m from Member m where m.username= :username and m.age= :age")
    List<Member> findUser(@Param("username") String username,
                          @Param("age") int age);


    /*******************************/

    // JPA의 값 타입 (@Embedded 방식도 이런 식으로 조회 가능함)
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // 이런 식으로 dto를 조회할 수도 있다.
    // DTO에 member-team이 존재하니까 둘을 join해서 가져와야 함
    // 단, DTO는 실제 엔티티가 아니기 때문에 new 명령어를 사용해야 한다. (+생성자도 필요)
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name)"
            + " from Member m join m.team t")
    List<MemberDto> findMemberDto();

    /*******************************/

    // 파라미터 바인딩
    // 위치 기반은 사용하지 말고, 이름 기반으로 사용하자.
    @Query("select m from Member m where m.username=:name")
    Member findMembers(@Param("name") String username);

    // 마찬가지로 컬렉션도 파라미터로 넘길 수 있다.
    // in으로 넘겨주면 됨!
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    /*******************************/

    // 페이징
    /*
        <<페이징과 정렬 파라미터>>
        org.springframework.data.domain.Sort : 정렬 기능
        org.springframework.data.domain.Pageable : 페이징 기능 (내부에 Sort 포함)

        <<특별한 반환 타입>>
        org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징 (totalCount 필요)
        org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능
        (내부적으로 limit + 1조회)

        List (자바 컬렉션): 추가 count 쿼리 없이 결과만 반환
     */

    // 여기서 Pageable은 인터페이스이다.
    // 실제로 사용할 때는 구현체인 PageRequest를 사용하는 형태.

    // 혹은, 이런 식으로 countQuery를 분리하는 방식도 존재한다.
    // 만약 join을 한다고 하면, count의 경우 굳이 join 할 필요가 없는데 한 번에 쓰면 count까지 조인하고 있어서
    // 상당한 성능 저하를 불러일으킬 수 있어서 아예 분리시키는 것.
    @Query(value = "select m from Member m left join m.team t",
    countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    /*******************************/

    // 벌크성 수정 쿼리
    // 이때 수정 후 영속성 컨텍스트 초기화를 하고 싶다면 clearAutomatically =true 옵션 켜주기
    // 이 옵션을 사용하지 않으면 1차 캐시에 과거의 변경 전 값이 남아있을 수 있어서
    // findById 같은 걸로 회원을 조회했을 때 원하지 않는 결과가 나올 수도 있으니 주의해야 함.
    @Modifying
    //@Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    // 참고로, @Param은 query 직접 작성 시 사용한다고 생각하기.
    int bulkAgePlus(@Param("age") int age);
    /*
        cf) 벌크 연산은 영속성 컨텍스트를 무시하기 때문에,
        영속성 컨텍스트에 있는 엔티티의 상태와 DB의 상태가 달라질 수 있다.

        - 웬만하면 영속성 컨텍스트에 엔티티가 없는 상태에서 벌크 연산을 먼저 수행할 것.
        - 엔티티가 있다면 벌크 연산 이후 영속성 컨텍스트를 초기화할 것
     */

    /*******************************/
    // JPQL 페치 조인
    // member 조회 시 연관된 team을 한 번에 가져오도록.
    // 이러면 나중에 team에서 프록시 객체가 만들어지는 게 아니라 그냥 바로 엔티티가 출력
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // spring data JPA는 JPA가 제공하는 엔티티 그래프 기능을 편리하게 사용하도록 도와준다.
    // JPQL 없이 페치 조인을 사용할 수 있도록!

    // 공통 메서드 오버라이드해주기
    @Override
    @EntityGraph(attributePaths = {"team"})
    // 이렇게 해주면 내부적으로 fetch join을 해준다.
    List<Member> findAll();

    // JPQL + 엔티티 그래프 (결과는 똑같음)
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 메서드 이름 이용해서도 할 수 있음!
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(String username);


    /*******************************/
    // JPA Hint -> JPA 구현체에게 제공하는 힌트.
    // 이런 식으로 해주면 readOnly=true로 설정했기 때문에 변경 x
    // forCounting을 사용하면 반환값이 Page일 때
    // 페이징을 위한 count 쿼리에 쿼리 힌트를 적용하게 된다 (기본값이 true임)

    // cf) readOnly=true면 스냅샷을 찍지 않는다. 여기서 스냅샷 = 변경 감지를 위해 원본을 복사해서 만들어두는 객체.
   @QueryHints(value = {@QueryHint(name="org.hibernate.readOnly",
                                    value = "true")}
                //,forCounting = true
                )
    Member findReadOnlyByUsername(String username);

    // Lock
    // select를 하면 끝부분에 for update를 붙여준다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

}

