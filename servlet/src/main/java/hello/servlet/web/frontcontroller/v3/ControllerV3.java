package hello.servlet.web.frontcontroller.v3;

import hello.servlet.web.frontcontroller.v2.ModelView;

import java.util.Map;

public interface ControllerV3 {
    // 서블릿을 사용하지 않기 때문에 코드가 간결화되었다.
    // httpServletRequest가 제공하는 파라미터는 프론트 컨트롤러가 paramMap에 담아서 호출해줄 예정
    // 응답 결과로 뷰 이름과 뷰에 전달한 model 데이터를 포함하는 modelView 객체를 반환해준다.
    ModelView process(Map<String, String> paramMap);
}
