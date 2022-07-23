package hello.proxy.pureproxy.concreteproxy;

import hello.proxy.pureproxy.concreteproxy.code.ConcreteClient;
import hello.proxy.pureproxy.concreteproxy.code.ConcreteLogic;
import hello.proxy.pureproxy.concreteproxy.code.TimeProxy;
import org.junit.jupiter.api.Test;

public class ConcreteProxyTest {
    @Test
    void noProxy() {
        ConcreteLogic concreteLogic = new ConcreteLogic();
        // 클라이언트가 구체 클래스 바로 접근
        ConcreteClient client = new ConcreteClient(concreteLogic);
        client.execute();
    }

    @Test
    void addProxy() {
        ConcreteLogic concreteLogic = new ConcreteLogic();
        TimeProxy timeProxy = new TimeProxy(concreteLogic);
        // 클라이언트가 프록시 주입.
        // client는 concreteLogic을 상속받고, timeProxy도 상속받기 때문에 주입이 가능. (다형성)
        // 타입과, 그 타입의 하위 타입은 모두 다형성의 대상이다!
        ConcreteClient client = new ConcreteClient(timeProxy);
        client.execute();
        /*
        00:41:20.816 [Test worker] INFO hello.proxy.pureproxy.concreteproxy.code.TimeProxy - TimeDecorator 실행
        00:41:20.821 [Test worker] INFO hello.proxy.pureproxy.concreteproxy.code.ConcreteLogic - ConcreteLogic 실행
        00:41:20.827 [Test worker] INFO hello.proxy.pureproxy.concreteproxy.code.TimeProxy - TimeDecorator 종료 resultTime=6
         */
    }
}
