package hello.itemservice.config;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.jpa.JpaItemRepositoryV3;
import hello.itemservice.repository.v2.ItemQueryRepositoryV2;
import hello.itemservice.repository.v2.ItemRepositoryV2;
import hello.itemservice.service.ItemServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
@RequiredArgsConstructor
public class V2Config {
    private final EntityManager em;
    private final ItemRepositoryV2 itemRepositoryV2;

    @Bean
    public ItemServiceV2 itemServiceV2() {
        // 여기서 그냥 itemRepositoryV2 주입 - 얘는 springDataJpa 관련된 거는 스프링이 자동으로 등록을 해주기 때문!
        return new ItemServiceV2(itemRepositoryV2, itemQueryRepositoryV2());
    }

    @Bean
    public ItemQueryRepositoryV2 itemQueryRepositoryV2() {
        return new ItemQueryRepositoryV2(em);
    }

    // 테스트에서 ItemRepository를 사용하기 때문에 얘도 빈 등록해두기
    @Bean
    public ItemRepository itemRepository() {
        return new JpaItemRepositoryV3(em);
    }
}
