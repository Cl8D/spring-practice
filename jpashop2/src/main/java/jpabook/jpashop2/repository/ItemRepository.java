package jpabook.jpashop2.repository;

import jpabook.jpashop2.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    // 상품 저장
    /*
    로직) 식별자 x -> 새로운 엔티티로 판단 -> persist() = 영속화
    식별자 o -> 기본에 영속화되었던 엔티티 -> merge() = 수정, 병합
    핵심은 merge의 경우 준영속 상태의 엔티티를 수정할 때 동작한다는 것.
    영속 상태의 엔티티는 변경 감지 기능을 통해서 트랜잭션 커밋 시 자동으로 수정된다.
    참고로, item의 식별자 (id)가 generatedValue로 설정되어 있어야 한다.
    -> 자동으로 생성되어야 항상 값이 있음을 보장하니까
    * */
    public void save(Item item) {
        // 만약 상품을 저장하려는데 존재하지 않는다면 persist.
        // (null값 업데이트 방지를 위해서 식별자 값이 없으면 영속화하고, 아니면 merge하는 형식으로!)
        if (item.getId() == null) {
            em.persist(item);
        }
        // merge의 경우 업데이트하는 거랑 비슷한 것 같다.
        // 준영속 상태의 엔티티를 받아서 새로운 영속 상태의 엔티티를 반환하는 메서드라고 함.
        else {
            // merge의 경우 동일한 식별자를 가진 아이템을 찾은 다음,
            // 파라미터로 넘긴 item의 값으로 해당 아이템의 성질을 다 바꿔준다.
            // 단, merge의 경우 모든 필드의 값을 수정하기 때문에 null이 들어갈 수 있음을 유의해야 한다.
            em.merge(item);

            // 추가적으로, 파라미터로 넘긴 item이 영속성 컨텍스트로 바뀌는 게 아니라는 점.
            // 단지 em.merge를 통해서 리턴된 값이 영속성 컨텍스트에 의해 관리되는 객체일 뿐!
        }
    }

    // 상품 한 개 조회
    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    // 상품 전체 조회
    public List<Item> findAll() {
        String query = "select i from Item i";
        return em.createQuery(query, Item.class)
                .getResultList();
    }
}
