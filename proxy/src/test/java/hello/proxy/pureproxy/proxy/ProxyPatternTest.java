package hello.proxy.pureproxy.proxy;

import hello.proxy.pureproxy.proxy.code.CacheProxy;
import hello.proxy.pureproxy.proxy.code.ProxyPatternClient;
import hello.proxy.pureproxy.proxy.code.RealSubject;
import hello.proxy.pureproxy.proxy.code.Subject;
import org.junit.jupiter.api.Test;

public class ProxyPatternTest {
    @Test
    void noProxyTest() {
        RealSubject realSubject = new RealSubject();
        ProxyPatternClient client = new ProxyPatternClient(realSubject);
        client.execute();
        client.execute();
        client.execute();
        /*
            client -> realSubject를 각각 3번 호출하기 때문에, 총 3초가 소요된다.
            그러나, 이 데이터가 변하지 않는 데이터라면 어딘가에 보관해두고 이미 조회된 데이터를 사용하는 것이 좋다.
            프록시 패턴은 주로 접근 제어를 다루며, 그 중 캐시가 존재한다. 캐시를 적용해보자.
         */
    }

    @Test
    void cacheProxyTest() {
        Subject realSubject = new RealSubject();
        Subject cacheProxy = new CacheProxy(realSubject);

        // 클라이언트는 프록시를 호출함.
        ProxyPatternClient client = new ProxyPatternClient(cacheProxy);
        client.execute();
        client.execute();
        client.execute();
        /*
            여기서는 초반에 저장하는 시간 1초, 이후에는 저장된 캐시값을 참조하기 때문에 즉시 참조.
            즉, 3초 => 1초로 줄어든 것을 확인할 수 있다.
         */
    }
}
