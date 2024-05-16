package memory;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * org.springframework.boot.autoconfigure.AutoConfiguration.imports
 * 이 파일에 등록하게 되면 스프링 부트가 시작 시점에 해당 파일의 정보를 읽어서 자동 구성 정보로 사용하게 된다.
 */
@AutoConfiguration
@ConditionalOnProperty(name = "memory", havingValue = "on")
public class MemoryAutoConfig {

    @Bean
    public MemoryController memoryController() {
        return new MemoryController(memoryFinder());
    }

    @Bean
    public MemoryFinder memoryFinder() {
        return new MemoryFinder();
    }
}
