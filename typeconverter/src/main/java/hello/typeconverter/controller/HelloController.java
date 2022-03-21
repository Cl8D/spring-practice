package hello.typeconverter.controller;

import hello.typeconverter.type.IpPort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HelloController {

    @GetMapping("/hello-v1")
    public String helloV1 (HttpServletRequest request) {
        // 문자 타입 조회
        // localhost:8080/hello-v1?data=10
        String data = request.getParameter("data");
        // 이를 숫자로 변경하기
        Integer intValue = Integer.valueOf(data);
        System.out.println("intValue = " + intValue);
        return "ok";

        // 보통 Http 요청 파라미터는 모두 문자이기 때문에,
        // 다른 타입으로 변환하고 싶다면 자바를 통해 타입 변환을 진행해야 한다.
    }

    // 혹은 requestParam을 사용하면 integer 타입의 data를 받을 수 있게 된다.
    // 이는 스프링이 알아서 타입을 변환해주는 것.
    @GetMapping("/hello-v2")
    public String helloV2 (@RequestParam Integer data) {
        System.out.println("data = " + data);
        return "ok";
    }

    @GetMapping("/ip-port")
    public String ipPort(@RequestParam IpPort ipPort) {
        System.out.println("ipPort.getIp() = " + ipPort.getIp());
        System.out.println("ipPort.getPort() = " + ipPort.getPort());
        return "ok";

        /**
         * http://localhost:8080/ip-port?ipPort=127.0.0.1:8080
         *
         * h.t.converter.StringToIpPortConverter    : convert source=127.0.0.1:8080
         *
         * ipPort.getIp() = 127.0.0.1
         * ipPort.getPort() = 8080
         */
    }
}
