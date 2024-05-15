package memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Slf4j
public class MemoryCondition implements Condition {

    /**
     * 'memory' 환경 설정 값이 'on'인 경우에만 메모리 정보를 확인한다.
     * -Dmemory=on 으로 설정
     */
    @Override
    public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
        final String memory = context.getEnvironment().getProperty("memory");
        log.info("memory: {}", memory);
        return "on".equals(memory);
    }
}
