package hello.servlet.web.frontcontroller.v4;

import java.util.Map;

// 이번에는 인터페이스에 modelView를 넣지 않는다.
// model 객체는 파라미터로 전달되기 때문에 그냥 사용하고, 결과로 뷰의 이름만 반환한다.
public interface ControllerV4 {

    /**
     *
     * @param paramMap
     * @param model
     * @return
     */

    // v3의 코드
    // ModelView process(Map<String, String> paramMap);
    // 이전에는 paramMap만 넘겼지만, 이제는 파라미터로 model이 넘어온다.
    // 즉, frontController가 모델까지 함께 념겨주는 구조.
    // 뷰의 이름을 반환하기 위해 리턴값이 string 형이다.
    String process(Map<String, String> paramMap, Map<String, Object> model);
}

