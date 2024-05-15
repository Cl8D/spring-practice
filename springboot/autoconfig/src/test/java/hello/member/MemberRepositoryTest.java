package hello.member;

import hello.EnableTestEnvironment;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@EnableTestEnvironment
@RequiredArgsConstructor
public class MemberRepositoryTest {

    private final MemberRepository memberRepository;

    @BeforeEach
    void init() {
        memberRepository.initTable();
    }

    @Transactional
    @Test
    void memberTest() {
        // given
        final Member member = new Member("idA", "memberA");

        // when
        memberRepository.save(member);

        // then
        final Member findMember = memberRepository.find(member.getMemberId());
        assertThat(findMember.getMemberId())
                .isEqualTo(member.getMemberId());

        assertThat(findMember.getName())
                .isEqualTo(member.getName());
    }

}
