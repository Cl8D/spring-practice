package hello.proxy.pureproxy.proxy.code;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CacheProxy implements Subject{
    private final Subject target;
    private String cacheValue;

    @Override
    public String operation() {
        log.info("프록시 호출");
        if(cacheValue == null)
            // 클라이언트가 프록시 호출했으니, 프록시는 실제 객체를 참조하도록.
            // 그리고 호출해서 리턴된 값을 변수에 저장해두기
            cacheValue = target.operation();
        return cacheValue;
    }
}
