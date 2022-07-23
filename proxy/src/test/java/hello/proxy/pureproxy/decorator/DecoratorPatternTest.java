package hello.proxy.pureproxy.decorator;

import hello.proxy.pureproxy.decorator.code.DecoratorPatternClient;
import hello.proxy.pureproxy.decorator.code.MessageDecorator;
import hello.proxy.pureproxy.decorator.code.RealComponent;
import hello.proxy.pureproxy.decorator.code.TimeDecorator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DecoratorPatternTest {
    @Test
    void noDecorator() {
        // 클라이언트가 실제 객체를 참조한 예시.
        RealComponent realComponent = new RealComponent();
        DecoratorPatternClient client = new DecoratorPatternClient(realComponent);
        client.execute();
        /*
        23:25:45.391 [Test worker] INFO hello.proxy.pureproxy.decorator.code.RealComponent - RealComponent 실행
        23:25:45.408 [Test worker] INFO hello.proxy.pureproxy.decorator.code.DecoratorPatternClient - result=data
         */
    }

    @Test
    void decorator1() {
        RealComponent realComponent = new RealComponent();
        // 데코레이터가 실제 객체를 알 수 있도록 의존관계 주입
        MessageDecorator messageDecorator = new MessageDecorator(realComponent);
        // 클라이언트는 데코레이터만 참조.
        DecoratorPatternClient client = new DecoratorPatternClient(messageDecorator);
        client.execute();

        /*
        23:37:28.932 [Test worker] INFO hello.proxy.pureproxy.decorator.code.MessageDecorator - MessageDecorator 실행
        23:37:28.947 [Test worker] INFO hello.proxy.pureproxy.decorator.code.RealComponent - RealComponent 실행
        23:37:28.963 [Test worker] INFO hello.proxy.pureproxy.decorator.code.MessageDecorator - MessageDecorator 꾸미기 적용 전=data, 적용 후=*****data*****
        23:37:28.968 [Test worker] INFO hello.proxy.pureproxy.decorator.code.DecoratorPatternClient - result=*****data*****
         */
    }

    @Test
    void decorator2() {
        RealComponent realComponent = new RealComponent();
        MessageDecorator messageDecorator = new MessageDecorator(realComponent);
        // message -> time을 참조할 수 있도록, 프록시가 프록시를 거쳐가는 형태!
        TimeDecorator timeDecorator = new TimeDecorator(messageDecorator);
        // 메시지 꾸미기 + 시간 측정까지 적용!
        DecoratorPatternClient client = new DecoratorPatternClient(timeDecorator);
        client.execute();

        /*
        23:41:02.709 [Test worker] INFO hello.proxy.pureproxy.decorator.code.TimeDecorator - TimeDecorator 실행
        23:41:02.721 [Test worker] INFO hello.proxy.pureproxy.decorator.code.MessageDecorator - MessageDecorator 실행
        23:41:02.721 [Test worker] INFO hello.proxy.pureproxy.decorator.code.RealComponent - RealComponent 실행
        23:41:02.738 [Test worker] INFO hello.proxy.pureproxy.decorator.code.MessageDecorator - MessageDecorator 꾸미기 적용 전=data, 적용 후=*****data*****
        23:41:02.754 [Test worker] INFO hello.proxy.pureproxy.decorator.code.TimeDecorator - TimeDecorator 종료 resultTime=33ms
        23:41:02.755 [Test worker] INFO hello.proxy.pureproxy.decorator.code.DecoratorPatternClient - result=*****data*****
         */
    }
}
