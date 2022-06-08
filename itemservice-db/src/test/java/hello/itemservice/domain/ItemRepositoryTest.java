package hello.itemservice.domain;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import hello.itemservice.repository.memory.MemoryItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ItemRepositoryTest {

    // 참고로, 테스트 시 구현체가 아니라 itemRepository 인터페이스를 테스트하는 걸 볼 수 있는데
    // 이러면 나중에 다른 구현체로 변경되었을 때 구현체가 잘 동작하는지 확인할 수 있다!
    @Autowired
    ItemRepository itemRepository;

    // 테스트가 서로 영향을 주지 않게 하기 위해서 테스트가 끝나면 실행
    @AfterEach
    void afterEach() {
        // 인터페이스에는 clearStore()가 없기 때문에, MemoryItemRepository인 경우만 제한적으로 사용
        if (itemRepository instanceof MemoryItemRepository) {
            ((MemoryItemRepository) itemRepository).clearStore();
        }
    }

    // 상품 하나 저장하고 잘 저장되었는지 테스트
    @Test
    void save() {
        //given
        Item item = new Item("itemA", 10000, 10);

        //when
        Item savedItem = itemRepository.save(item);

        //then
        Item findItem = itemRepository.findById(item.getId()).get();
        assertThat(findItem).isEqualTo(savedItem);
    }

    // 상품 저장 후 업데이트 -> 잘 되었는지 확인
    @Test
    void updateItem() {
        //given
        Item item = new Item("item1", 10000, 10);
        Item savedItem = itemRepository.save(item);
        Long itemId = savedItem.getId();

        //when
        ItemUpdateDto updateParam = new ItemUpdateDto("item2", 20000, 30);
        itemRepository.update(itemId, updateParam);

        //then
        Item findItem = itemRepository.findById(itemId).get();
        assertThat(findItem.getItemName()).isEqualTo(updateParam.getItemName());
        assertThat(findItem.getPrice()).isEqualTo(updateParam.getPrice());
        assertThat(findItem.getQuantity()).isEqualTo(updateParam.getQuantity());
    }

    // 상품 검색 테스트
    @Test
    void findItems() {
        //given
        Item item1 = new Item("itemA-1", 10000, 10);
        Item item2 = new Item("itemA-2", 20000, 20);
        Item item3 = new Item("itemB-1", 30000, 30);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        //둘 다 없음 검증 - 문자인 경우 null, ""(빈 문자)인 경우 2가지 모두 체크
        // 세 번째 파라미터부터 검색 조건에 해당하는 item들 넘겨주면서 검증하도록
        test(null, null, item1, item2, item3);
        test("", null, item1, item2, item3);

        //itemName 검증
        test("itemA", null, item1, item2);
        test("temA", null, item1, item2);
        test("itemB", null, item3);

        //maxPrice 검증
        test(null, 10000, item1);

        //둘 다 있음 검증
        test("itemA", 10000, item1);
    }

    void test(String itemName, Integer maxPrice, Item... items) {
        // 검색 조건 DTO 만들어서 findAll로 직접 검색해보기
        List<Item> result = itemRepository.findAll(new ItemSearchCond(itemName, maxPrice));
        // 파라미터로 넘어온 item 리스트와 동일한지 체크 = 검색이 잘 되었는지!
        // 참고로 containsExactly를 사용하면 순서도 동일해야 함!!
        assertThat(result).containsExactly(items);
    }
}
