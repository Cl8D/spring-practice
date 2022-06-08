package hello.itemservice.domain;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import hello.itemservice.repository.memory.MemoryItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/*
    테스트의 원칙.
    1) 다른 테스트와 격리해야 한다
    2) 반복해서 실행할 수 있어야 한다.

 */
// @SpringBootApplication을 찾아서 설정으로 사용함!
@SpringBootTest
@Transactional
class ItemRepositoryTest {

    // 참고로, 테스트 시 구현체가 아니라 itemRepository 인터페이스를 테스트하는 걸 볼 수 있는데
    // 이러면 나중에 다른 구현체로 변경되었을 때 구현체가 잘 동작하는지 확인할 수 있다!
    @Autowired
    ItemRepository itemRepository;

//    // 트랜잭션 관련 코드
//    @Autowired
//    PlatformTransactionManager transactionManager; // 트랜잭션 관리자는 얘를 주입받아서 사용함. (스프링 부트의 경우 자동으로 적절한 트랜잭션 매니저를 스프링 빈으로 등록해준다!)
//    TransactionStatus status;
//
//    // 각각의 테스트 케이스를 실행하기 전에 호출된다.
//    @BeforeEach
//    void beforeEach() {
//        // 트랜잭션 시작 -> 각 테스트를 트랜잭션 범위 안에서 실행 가능!
//        status = transactionManager.getTransaction(new DefaultTransactionDefinition());
//    }
    // 스프링은 이 과정을 @Transactional으로 해결해준다!

    // 테스트가 서로 영향을 주지 않게 하기 위해서 테스트가 끝나면 실행
    @AfterEach
    void afterEach() {
        // 인터페이스에는 clearStore()가 없기 때문에, MemoryItemRepository인 경우만 제한적으로 사용
        if (itemRepository instanceof MemoryItemRepository) {
            ((MemoryItemRepository) itemRepository).clearStore();
        }

//        // 트랜잭션 롤백
//        transactionManager.rollback(status);
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
