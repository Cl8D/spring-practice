package jpabook.jpashop3.service;

import jpabook.jpashop3.domain.Member;
import jpabook.jpashop3.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 가입
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    // 회원 수정
    @Transactional
    public void update(Long id, String name) {
        // 여기서 JPA에서 최초로 찾을 때는 1차 캐시에 없으니깐 DB에서 찾아서 가져오고
        // 영속 상태의 member를 set으로 이름을 바꿔주게 되면
        // aop를 동작하면서 커밋이 되면서 변경 감지로 인해 flush 후 db 커밋을 통해 변경 내용을 반영해준다!
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }

    // 중복 회원 검증
    private void validateDuplicateMember(Member member){
        List<Member> findMembers =
                memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty())
            throw new IllegalStateException("이미 존재하는 회원입니다.");
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 한 명 조회
    public Member findOne(Long id) {
        return memberRepository.findOne(id);
    }


}
