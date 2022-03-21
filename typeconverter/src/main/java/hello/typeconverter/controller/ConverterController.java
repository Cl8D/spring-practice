package hello.typeconverter.controller;

import hello.typeconverter.type.IpPort;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ConverterController {

    @GetMapping("/converter-view")
    public String converterView(Model model) {
        // model에 숫자 10000과 ipPort 객체 담아서 뷰에게 전달
        model.addAttribute("number", 1000);
        model.addAttribute("ipPort", new IpPort("127.0.0.1", 8080));
        return "converter-view";
    }

    // ipPort를 뷰 템플릿 폼에 출력해주기
    @GetMapping("/converter/edit")
    public String converterForm (Model model) {
        IpPort ipPort = new IpPort("127.0.0.1", 8080);
        Form form = new Form(ipPort);

        model.addAttribute("form", form);
        return "converter-form";
    }

    // 뷰 템플릿 폼의 ipPort 정보를 받아서 출력해주기
    @PostMapping("/converter/edit")
    // html에 있는 제출 버튼을 누르면 string 형이 전달되지만,
    // 이때 받아올 때 form형으로 받는데 그 내부에 ipPort형이 있으니까
    // stringToIpPort를 통해 IpPort형으로 바뀐다.
    // 그냥 modelAttribute이 컨버전 서비스를 해줬다고 생각하면 됨!
    public String converterEdit (@ModelAttribute Form form, Model model) {
        IpPort ipPort = form.getIpPort();
        model.addAttribute("ipPort", ipPort);
        return "converter-view";
    }


    // 데이터를 전달하는 객체로써 Form 사용
    @Data
    static class Form {
        private IpPort ipPort;

        public Form(IpPort ipPort) {
            this.ipPort = ipPort;
        }
    }
}


