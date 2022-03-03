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
    public void save(Item item) {
        // 만약 상품을 저장하려는데 존재하지 않는다면 persist.
        if (item.getId() == null) {
            em.persist(item);
        }
        // merge의 경우 업데이트하는 거랑 비슷한 것 같다.
        // 준영속 상태의 엔티티를 받아서 새로운 영속 상태의 엔티티를 반환하는 메서드라고 함.
        else {
            em.merge(item);
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
