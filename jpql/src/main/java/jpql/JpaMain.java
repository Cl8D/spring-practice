package jpql;

import javax.persistence.*;
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
            

            tx.commit();
        }catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
