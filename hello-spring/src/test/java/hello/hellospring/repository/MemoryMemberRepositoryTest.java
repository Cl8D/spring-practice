package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.apache.el.parser.AstSetData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class MemoryMemberRepositoryTest {

    MemoryMemberRepository repository = new MemoryMemberRepository();

    @AfterEach
    public void afterEach() {
        // afterEach annotation을 사용하면 각 테스트 메서드 이후에 실행됨
        repository.clearStore();
    }


    @Test
    public void save() {
        Member member = new Member();
        member.setName("spring");
        repository.save(member);

        // 반환 타입이 optional이기 때문에 get()을 사용해서 꺼내기
        Member result = repository.findById(member.getId()).get();

        // 넣은 것과 저장된 게 같은지 화인하기
        // System.out.println("result = " + (result == member));

        // 둘이 똑같은지 확인 (member와 result)
        //Assertions.assertEquals(member, result);

        assertThat(result).isEqualTo(result);
    }

    @Test
    public void findByName() {
        // shift + F6 -> 일괄 rename
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("spring2");
        repository.save(member2);

        Member result = repository.findByName("spring1").get();

        assertThat(result).isEqualTo(member1);

    }

    @Test
    public void findAll() {
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("spring1");
        repository.save(member2);

        List<Member> result = repository.findAll();
        assertThat(result.size()).isEqualTo(2);

    }
}
