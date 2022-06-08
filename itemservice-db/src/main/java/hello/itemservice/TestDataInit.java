package hello.itemservice;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@RequiredArgsConstructor
public class TestDataInit {

    private final ItemRepository itemRepository;

    /**
     * 확인용 초기 데이터 추가
     */
    // 스프링 컨테이너가 "완전히" 초기화를 끝내고(AOP 포함), 실행 준비가 되었을 때 발생하는 이벤트
    // 스프링은 이때 이 어노테이션이 붙은 initData()를 호출한다.

    // @PostConstruct를 사용하면 되지 않을까?
    // -> AOP 같은 부분이 처리되지 않은 시점에서 호출될 수 있기 때문에, 문제가 발생할 수도 있음
    // (ex. @Transactional과 관련된 AOP들이 적용되지 않았을 때 호출될지도)
    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        log.info("test data init");
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }

}
