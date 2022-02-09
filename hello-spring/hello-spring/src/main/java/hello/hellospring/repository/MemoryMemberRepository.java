package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.*;


// 마찬가지로 repository로 해줘야 한다!
// @Repository
public class MemoryMemberRepository implements MemberRepository {

    // 정보를 저장해줄 공간
    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L; // 키값을 생성해주는 애

    @Override
    public Member save(Member member) {
        // id를 sequence 변수를 이용해서 설정
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        // null을 위해 optional.ofNullable 사용
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByName(String name) {
        // stream은 컬랙션, 배열 등의 저장 요소를 하나씩 참조하며 람다식을 적용하여 반복적으로 처리할 수 있도록 해주는 기능
        return store.values().stream()
                // 즉, member의 getName를 통해서 얻은 이름이 파라미터 name과 동일한지 filter 걸어주기
                .filter(member -> member.getName().equals(name))
                // findAny는 stream에서 가장 먼저 탐색되는 요소를 리턴한다
                .findAny();

    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    // 하나의 test 실행 후 store를 비어주는 함수
    public void clearStore() {
        store.clear();
    }
}
