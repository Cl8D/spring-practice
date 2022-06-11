package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataJpaItemRepository extends JpaRepository<Item, Long> {

    // 아이템 이름으로 검색
    // select i from Item i where i.name like ?
    List<Item> findByItemNameLike(String itemName);

    // 가격 조건 (price보다 더 작은 item 리스트 검색)
    // select i from Item i where i.price <= ?
    List<Item> findByPriceLessThanEqual(Integer price);

    // 아이템 이름 + 가격 조건 동시 처리
    // select i from Item i where i.itemName like ? and i.price <= ?
    // 메서드 이름으로 하는 버전. (조인을 사용할 수가 없다는 단점 + 메서드 이름이 너무 길어짐)
    List<Item> findByItemNameLikeAndPriceLessThanEqual(String itemName, Integer price);

    // 위와 동일하지만 쿼리로 직접 작성
    @Query("select i from Item i where i.itemName like :itemName and i.price <= :price")
    List<Item> findItems(@Param("itemName") String itemName, @Param("price") Integer price);

}
