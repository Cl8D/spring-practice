package hello.servlet.domain.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 동시성 문제가 고려되어 있지 않음, 실무에서는 ConcurrentHashMap, AtomicLong 사용 고려
 */
public class MemberRepository {

    private static Map<Long, Member> store = new HashMap<>();
    private static Long sequence = 0L;

    // 싱글톤으로 생성
    private static final MemberRepository instance = new MemberRepository();

    // 싱글톤 객체를 조회할 때 무조건 얘를 거치도록
    public static MemberRepository getInstance() {
        return instance;
    }

    // 싱글톤이기 때문에 아무나 생성하지 못하게 하기 위해서 private으로 선언
    private MemberRepository() {

    }

    // 1) 회원 저장
    public Member save(Member member){
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    // 2) 회원 목록 조회 - 아이디
    public Member findById (Long id) {
        return store.get(id);
    }

    // 조회 - 전체
    public List<Member> findAll () {
        return new ArrayList<>(store.values());
    }

    // 저장 목록 초기화
    public void clearStore() {
        store.clear();
    }


}
