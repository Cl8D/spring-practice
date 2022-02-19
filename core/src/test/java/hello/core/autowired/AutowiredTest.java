package hello.core.autowired;

import hello.core.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.Nullable;

import java.util.Optional;

public class AutowiredTest {

    @Test
    void AutowiredOption() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestBean.class);

    }

    static class TestBean {
        // 자동 주입 대상을 옵션으로 처리하는 방법 3가지

        // 1. autowired의 파라미터 required = false 설정
        // Member 클래스는 스프링 빈이 아니기 때문에 스프링 컨테이너가 관리 x
        @Autowired(required = false)
        public void setNoBean1 (Member noBean1) {
            System.out.println("noBean1 = " + noBean1);
        }
        // 출력 결과) 의존 관계 자체가 없기 때문에 아예 호출이 되지 않는다

        // 2. Null 호출 (자동 주입 대상이 없으면 null이 호출됨)
        @Autowired
        public void setNoBean2 (@Nullable Member noBean2) {
            System.out.println("noBean2 = " + noBean2);
        }
        // 출력 결과) 호출은 되지만 null로 찍힌다

        // 3. optional 사용
        @Autowired
        public void setNoBean3 (Optional<Member> noBean3) {
            System.out.println("noBean3 = " + noBean3);
        }
        // 출력 결과 ) Optional.empty로 나온다
    }
}
