package hello.servlet.web.frontcontroller.v2;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    // 뷰의 논리적 이름
    private String viewName;
    // 컨트롤러에서 뷰에 필요한 데이터를 key-value 값으로 넣어주기
    // 뷰의 이름(string) - 뷰를 렌더링할 때 필요한 객체 (object)가 map으로 들어가 있음
    private Map<String, Object> model = new HashMap<>();

    public ModelView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
}
