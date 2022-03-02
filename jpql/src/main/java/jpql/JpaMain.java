package jpql;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{

            // 기본 문법과 쿼리 API
            /*
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);
            */
            /*
            // member1 하나만 있기 때문에 단일 출력.
            Member result = em.createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", "member1")
                    .getSingleResult();
            System.out.println("result.getUsername() = " + result.getUsername());
            */

            //em.flush();
            //em.clear();

            // 프로젝션 (select)
            // 여기서 Member m은 엔티티이기 때문에, 반환 역시 엔티티들이 나오게 되는데,
            // 이러면 얘는 영속성 컨텍스트에 관리가 될까?
            /*
            List<Member> result = em.createQuery("select m from Member m", Member.class)
                    .getResultList();

            // 확인 결과, member1의 age가 10->20으로 변경된다,
            // 즉, 영속성 컨텍스트에 의해 관리된다는 의미이다.
            Member findMember = result.get(0);
            findMember.setAge(20);
            */

            /*
            // 여러 타입을 같이 뽑아서 타입을 알 수 없는 경우
            // 이렇게 새로운 클래스를 만들어서 생성자를 이용하기
            // 대신 패키지명을 입력해야 해서 좀 복잡해짐.
            List<MemberDTO> result = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m",
                    MemberDTO.class).getResultList();
            MemberDTO memberDTO = result.get(0);
            System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
            System.out.println("memberDTO.getAge() = " + memberDTO.getAge());
            */

            /*
            // 페이징
            // 1번째부터 10개를 가져오고 싶다면?
            for(int i=0; i<100; i++){
                Member member = new Member();
                member.setUsername("member" + i);
                member.setAge(i);
                em.persist(member);
            }

            em.flush();
            em.clear();

            List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(1)
                    .setMaxResults(10)
                    .getResultList();
            System.out.println("result.size() = " + result.size());

            for (Member member1 : result) {
                System.out.println("member1 = " + member1);
            }
            */


            /*
            // 조인
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("1");
            member.setAge(10);
            member.setTeam(team);
            member.setType(MemberType.ADMIN);
            em.persist(member);

            em.flush();
            em.clear();
            */

            /*
            // member-team을 inner join해주기
            String query = "select m from Member m inner join m.team t";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();
            System.out.println("result.size() = " + result.size());

            for (Member member1 : result) {
                System.out.println("member1 = " + member1);
            }
            */
            
            
            // JPQL 타입 표현
            // enum을 표현할 때 패키지명까지 써줘야 한다는 걸 주의하자.
            /*
            String query = "select m.username, 'HELLO', TRUE from Member m " +
                    "where m.type = jpql.MemberType.ADMIN";
            List<Object []> result = em.createQuery(query).getResultList();
            */

            /*
            // 혹은 이런 식으로 파라미터 바인딩을 해줄 수 있다.
            String query = "select m.username, 'HELLO', true from Member m " +
                    "where m.type = :userType";
            List<Object[]> result = em.createQuery(query)
                    .setParameter("userType", MemberType.ADMIN)
                    .getResultList();

            // 출력 결과: teamA, HELLO, true.
            for (Object[] objects : result) {
                System.out.println("objects[0] = " + objects[0]);
                System.out.println("objects[1] = " + objects[1]);
                System.out.println("objects[2] = " + objects[2]);
            }
            */

            // 페치 조인 (fetch join)
            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);


            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("member3");
            member3.setTeam(teamB);
            em.persist(member3);

            em.flush();
            em.clear();

            // before Fetch Join
            // String query = "select m from Member m";

            // After Fetch Join (Entity)
            //String query = "select m from Member m join fetch m.team";

            /*
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();

            // 여기서 member.getTeam().getName()을 할 때마다 db에 쿼리를 날리게 된다.
            // 조금 더 구체적으로 말하자면,
            // 회원1, 팀A -> SQL을 날려서 가져옴
            // 회원2, 팀A -> 회원1 덕분에 팀A는 이미 1차 캐시에 저장되어 있기 때문에 1차 캐시에서 가져옴
            // 회원3, 팀B -> 얘는 없으니깐 또 쿼리를 날려서 가져옴
            // 즉, 쿼리가 여러 번 나가게 되니까...
            // N+1 문제가 발생하게 된다. => 이를 위해 페치 조인 사용하기
            // fetch join을 활용하면 쿼리가 1번만 나오게 된다. (조인으로 한 번에 가져옴)
            for (Member member : result) {
                System.out.println("member = " + member.getUsername() + ", "
                        + member.getTeam().getName());
            }
            */

            // Collection Fetch Join
            //String query = "select t from Team t join fetch t.members";

            // distinct 활용하기 -> 중복 제거
            String query = "select distinct t from Team t join fetch t.members";

            List<Team> result = em.createQuery(query, Team.class).getResultList();

            // 출력을 해보면 teamA의 경우가 2번 출력이 되는데,
            // db 입장에서는 일대다를 하다보면 데이터가 뻥튀기 된다.
            // (왜냐면, teamA에 회원 2명이 있는 걸 db에 저장하면 각각 한 줄씩 2줄로 저장이 되니깐, jpa는 이를 구분 못하고 그대로 가져옴)
            // distinct를 활용하면 팀A, 팀B 한 번씩만 나오게 된다.
            for (Team team : result) {
                System.out.println("team = " + team.getName() + "|" + team.getMembers().size());

            }





            em.flush();
            em.clear();

            tx.commit();
        }catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
