package hello.core.scan.filter;

import java.lang.annotation.*;

// Target 지정시 type으로 하면 클래스 레벨에 붙음을 의미한다.
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyExcludeComponent {
    // 얘가 붙으면 컴포넌트 스캔에서 제외

}
