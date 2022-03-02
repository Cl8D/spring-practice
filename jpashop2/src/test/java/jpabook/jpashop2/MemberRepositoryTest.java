package jpabook.jpashop2;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

// JUnit에게 spring과 관련된 걸 테스트함을 알려주기
@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {

    // autowired -> Spring DI에서 사용하는 어노테이션.
    // 변수 및 메서드에 스프링이 관리하는 Bean을 자동으로 매핑해주는 느낌.
    // Autowired의 경우 타입으로 의존성을 주입한다.
    @Autowired
    MemberRepository memberRepository;

    @Test
    // entityManager를 통한 모든 데이터 변경은 항상 트랜잭션 안에서 일어나야 한다.
    // 참고로, 해당 어노테이션이 test에 있으면 테스트 종료 후 롤백을 해버린다. (db 롤백)
    // 이를 원하지 않는다면 rollback 어노테이션 활용하기
    @Transactional
    @Rollback(false)
    public void testMember() throws Exception {
        // given
        Member member = new Member();
        member.setUsername("memberA");

        // when
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.find(savedId);

        // then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());

        // findMember랑 Member는 같은 트랜잭션 안에서 저장하고 조회하였기 때문에
        // 영속성 컨텍스트가 동일함을 보장한다. (식별자가 같으면 동일한 엔티티로 인식한다. 1차 캐시에서 가져오니까)
        Assertions.assertThat(findMember).isEqualTo(member);
    }



}