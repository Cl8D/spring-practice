package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        // persistence.xml에 있는 persistence-unit name=""을 파라미터로 넘긴다.

        // EMF는 하나만 생성해서 애플리케이션 전체에서 공유된다.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        // EM은 스레드간에는 공유하지 않는다.
        // 사용 후에 꼭 닫아줘야 한다!
       EntityManager em = emf.createEntityManager();

        // 중요. 데이터를 변경하는 모든 작업은 JPA에서 꼭 트랜잭션 내부에서 진행해야 함!
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 여기서 실제로 동작하는 코드 작성
            /*
            Member member = new Member();
            member.setId(1L);
            member.setName("HelloA");

            // 저장
            em.persist(member);

            // 조회
            // PK로 멤버를 찾을 수 있다.
            Member findMember = em.find(Member.class, 1L);

            // 삭제 - 조회한 애를 삭제해주면 된다.
            // em.remove(findMember);

            // 수정
            // 수정 후에 굳이 em.persist으로 저장을 하지 않아도 된다는 점!
            // JPA가 커밋시점에 바뀌었는지 확인하고 update query를 날리기 때문에 이럴 수 있는 것
            findMember.setName("HelloJPA");
            */

            /*
            // 1. 비영속 상태
            Member member = new Member();
            member.setId(101L);
            member.setName("HelloJPA2");

            // 2. 영속 상태 -> 영속성 컨텍스트에 의해 member 관리
            // 실질적으로 db에 저장되는 상태는 아님.
            // 출력을 해보면 이 사이에서 query문이 나오지 않음.
            System.out.println("==BEFORE==");
            em.persist(member);
            System.out.println("==AFTER==");

            // 조회
            // 마찬가지로 조회를 했을 때 query문이 나오지 않는다.
            // 왜냐면, 1차 캐시에서 받아오는 거니까.
            Member findMember = em.find(Member.class, "101L");
            System.out.println("findMember.getId() = " + findMember.getId());
            System.out.println("findMember.getName() = " + findMember.getName());
            */

            // <영속성 컨텍스트 장점>
            // 1) 1차 캐시 이용
            // 똑같은 걸 2번 조회했을 때
            // 처음 거는 DB에서 가져오고, 두 번째부터는 1차 캐시에서 뒤진다.
            // 그렇기 때문에 hibernate query가 딱 1번만 되는 걸 출력 결과를 볼 수 있다.
            /*
            Member findMember1 = em.find(Member.class, 101L);
            Member findMember2 = em.find(Member.class, 101L);

            // 2) 동일성 보장
            // ==으로 비교했을 때 같음을 보장함 (result = true)
            System.out.println("result = " + (findMember1 == findMember2));
            */

            // 3) 쓰기 지연
            /*
            Member member1 = new Member(150L, "A");
            Member member2 = new Member(160L, "B");

            // 차곡차곡 쌓이게 된다.
            // 실행 결과를 확인해보면 sout으로 출력해준 결과 다음에 쿼리가 만들어진다.
            // 즉, 커밋이 되는 시점에 쿼리가 생성되는 것.
            // 쌓이는 옵션은 persistence.xml의 옵션에서 hibernate.jdbc.batch_size를 조절해주면 된다.
            em.persist(member1);
            em.persist(member2);
            System.out.println("======이 안에 쿼리가 생성되는가?======");
            */

            /*
            // 4) 변경 감지
            Member member = em.find(Member.class, 150L);
            member.setName("ZZZZZ");
            // 참고로, persist를 호출해줄 필요는 없다.
            // 알아서 JPA에서 처리를 해주니까.
            */

            /*
            // 플러시 실습
            Member member = new Member(200L, "member200");
            em.persist(member);

            // 쿼리가 날라가는 걸 미리 보고 싶다면!
            // 강제로 플러시를 해주기 -> 이때 DB에 insert query가 바로 나감.
            em.flush();
            // 플러시를 한다고 해서 1차 캐시가 없어지는 게 아니라,
            // 변경 감지 및 쓰기 지연 SQL 저장소에 있는 애들이 DB에 반영되는 것!
            // 결과를 확인해보면 commit 될 때가 아닌 sout문 이전에 쿼리문이 호출되는 걸 볼 수 있다.
            System.out.println("===이 선 이전에 쿼리문이 호출되는가?====");
            */

            /*
            // 준영속 상태 실습
            // 현재 이 멤버는 영속 상태이다. (기존에 등록했던 애를 조회하니까 1차 캐시에서 가져옴)
            Member member = em.find(Member.class, 150L);
            member.setName("AAAAA");

            // 더 이상 영속성 컨텍스트가 관리하지 않았으면 좋겠다면?
            //em.detach(member);
            // 이렇게 되면 커밋을 할 때 아무 일도 일어나지 않는다. jPA가 관리하지 않으니까.
            // 출력을 해보면 .find()에 의해 select 쿼리는 나오지만.
            // setName()으로 인한 update query는 나오지 않는 것을 확인할 수 있다.

            // 영속성 컨텍스트 초기화
            em.clear();
            // 이후 똑같은 멤버를 다시 조회하게 된다면, 1차 캐시가 비어있기 때문에 재등록하게 된다.
            // 결과적으로 2번의 쿼리문이 출력됨. (위에서 em.find 했을 때 + 지금)
            Member member2 = em.find(Member.class, 150L);
            */

            /*
            // 기본 키 매핑
            Member member = new Member();
            member.setUsername("C");

            em.persist(member);
            */

            // 외래 키 식별자를 직접 다루기
            Team team = new Team();
            team.setName("TeamA");

            // 영속 상태가 되면 pk 값이 세팅되기 때문에
            // id가 알아서 설정이 된다.
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            //member.setTeamId(team.getId());

            // 이제 이렇게만 해주면 JPA가 알아서 매핑을 시켜준다.
            // member.changeTeam(team);
            em.persist(member);

            // 양방향 연관 관계. 위의 코드 대신에 사용한 것.
            team.addMember(member);

            // 1차 캐시에서 가져옴을 방지 (db에서 가져오도록)
            em.flush();
            em.clear();


            // 단방향 연관 관계
            // 식별자로 조회하는 방법
            // 연관관계 없이 조회하다 보니까 객체 지향스럽지 않다.
            /*
            Member findMember = em.find(Member.class, member.getId());

            //Long findTeamId = findMember.getTeamId();
            //Team findTeam = em.find(Team.class, findTeamId);

            // 마찬가지로 위의 두 줄을 한 줄로 줄일 수 있다.
            // 바로 team을 가져올 수 있게 된다.
            Team findTeam = findMember.getTeam();
            */

            // 양방향 연관관계
            /*
            Member findMember = em.find(Member.class, member.getId());

            List<Member> members = findMember.getTeam().getMembers();

            for(Member m : members) {
                System.out.println("m.getUsername() = " + m.getUsername());
            }
            */





            // 커밋 -> 이때 db에 쿼리가 날라가서 저장되는 것임.
            tx.commit();

        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }
}
