package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repositoryV0 = new MemberRepositoryV0();

    @Test
    public void crud() throws Exception {
        // save test
        Member member = new Member("memberV100", 10000);
        repositoryV0.save(member);

        // findById test
        Member findMember = repositoryV0.findById(member.getMemberId());
        // 16:20:43.799 [main] INFO hello.jdbc.repository.MemberRepositoryV0Test - findMember=Member(memberId=memberV0, money=10000)
        // 참고로, lombok의 toString 때문에 이런 식으로 나온 것임!
        log.info("findMember={}", findMember);

        // cf) member==findMember => false
        // member.equals(findMember) => true -> lombok 내부의 equals를 사용하여 비교하기 때문에
        // Data annotation 사용시 equals, hashcode 모두 지원
        // 마찬가지로 isEqualTo도 equals를 써서 비교하기 때문에 동일한 객체 되는 것
        assertThat(findMember).isEqualTo(member);

        // update test: money(10000->20000)
        repositoryV0.update(member.getMemberId(), 20000);
        Member updatedMember = repositoryV0.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        // delete test
        repositoryV0.delete(member.getMemberId());
        // exception을 이용해서 tdd 짜기!
        Assertions.assertThatThrownBy(() ->
                repositoryV0.findById(member.getMemberId()))
                        .isInstanceOf(NoSuchElementException.class);

    }

}