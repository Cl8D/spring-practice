package hello.itemservice.repository;

import hello.itemservice.domain.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    // 상품 저장
    Item save(Item item);

    // 상품 변경
    void update(Long itemId, ItemUpdateDto updateParam);

    // 상품 단건 조회
    Optional<Item> findById(Long id);

    // 상품 전체 조회 (검색에 사용)
    List<Item> findAll(ItemSearchCond cond);

}
