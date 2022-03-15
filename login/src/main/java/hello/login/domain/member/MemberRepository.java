package hello.login.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class MemberRepository {
    private Map<Long, Member> store = new HashMap<>();
    private static Long sequence = 0L;

    public Member save(Member member) {
        member.setId(++sequence);
        log.info("save: member={}", member);
        store.put(member.getId(), member);
        return member;
    }

    public Member findById(Long id) {
        return store.get(id);
    }

    // Optinal을 쓰면 그 안에 회원 객체가 있을 수도, 없을 수도 있음.
    // 무엇보다 filter 기능을 쓸 수 있는 게 유용한 것 같음!!
    public Optional<Member> findByLoginId(String loginId) {
        // findAll을 통해 찾아낸 member 리스트에서 loginId를 가져온 다음에,
        // 파라미터로 받은 loginId와 동일한 멤버를 찾는 것.
        // 그 중에서 가장 처음 것을 리턴.
        // .stream().filter(조건) => 이게 문법이니까 참고!!
        return findAll().stream()
                .filter(m -> m.getLoginId().equals(loginId))
                .findFirst();
    }

    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }
}
