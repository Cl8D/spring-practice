package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

// @service를 활용하면 spring container에 memberserivce를 등록할 수 있음!
// @Service

// JPA를 사용하기 위해서는 트랜잭션이 항상 있어야 함
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    // 외부에서 memberRepository를 받아오는 형태로 변경
    // dependency injection (di)라고도 한다!
    // memberservice는 memberRepository가 필요하기 때문에,
    // @Autowired를 작성하여 생성할 때 컨테이너에 memberRepository를 넣어줌. (정확하게는 구현체인 memoryMemberRepository)
    // @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    // 회원 가입
    public Long join (Member member) {
        // 같은 이름이 있는 중복 회원 방지
        // ifPresent를 사용하면 null을 체크하는 if문을 안 사용할 수 있음!
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
        /*
        // 기본 시간 측정 코드
        long start = System.currentTimeMillis();
        try {
            // 같은 이름이 있는 중복 회원 방지
            // ifPresent를 사용하면 null을 체크하는 if문을 안 사용할 수 있음!
            validateDuplicateMember(member);
            memberRepository.save(member);
            return member.getId();
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println("join = " + timeMs + "ms");
        }
        */

    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    // 전체 회원 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // id를 통해서 회원 조회하기
    public Optional<Member> findOne(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
