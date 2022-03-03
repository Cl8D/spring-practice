package jpabook.jpashop2.service;

import jpabook.jpashop2.domain.Member;
import jpabook.jpashop2.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 마찬가지로 @service에도 @Component가 내부에 포함되어 있기 때문에 컴포넌트 스캔의 대상이 된다.
// 서비스 계층은 사용자가 제공받는 기능의 모음이라고도 볼 수 있음
@Service
// JPA의 데이터 변경은 모두 트랜잭션 내부에서 발생해야 한다.
// @Transactional의 readOnly 옵션을 true로 해주면 읽기 전용으로 만들어서 조금 더 성능이 최적화된다.
@Transactional(readOnly = true)
// @AllArgsConstructor -> 생성자 자동 생성. DI 진행을 생성자에서 진행해주며,
// 생성자가 1개면 어차피 autowired가 알아서 진행되기 때문에 생략이 가능해진다.
// 여기서 조금 발전시켜, @RequiredArgsConstructor의 경우 final 필드만 가지고 생성자를 만들어준다.(lombok)
@RequiredArgsConstructor
public class MemberService {

    // 변경할 일이 없기 때문에 final로 설정해주기
    // final로 설정하면 컴파일 시점에 memberRepository를 설정하지 않는 오류를 체크 가능
    // (원래를 기본 생성자를 추가할 때 많이 발견한다)
    private final MemberRepository memberRepository;

    // 회원 가입
    // 데이터를 수정 과정이 들어가기 때문에 readOnly 옵션 적용 x
    @Transactional
    public Long join(Member member){
        // 같은 이름을 가지고 있는 회원은 가입 불가능
        validateDuplicateMember(member);
        // save 함수 내부에서 em.persist가 진행
        memberRepository.save(member);
        return member.getId();
    }

    // 중복 회원 검증
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }

    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 한 명 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

}


