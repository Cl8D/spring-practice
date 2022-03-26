package jpabook.jpashop3.repository;

import jpabook.jpashop3.domain.item.Item;
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
        if (item.getId() == null)
            em.persist(item);
        else
            em.merge(item);
    }

    // 상품 한 개 조회
    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    // 상품 전체 조회
    public List<Item> findAll() {
        String query = "select i from Item i";
        return em.createQuery(query, Item.class)
                .getResultList();
    }
}
