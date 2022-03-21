package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/servlet/v1")
public class ServletUploadContollerV1 {
    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFileV1 (HttpServletRequest request) throws ServletException, IOException {
        // request=org.springframework.web.multipart.support.StandardMultipartHttpServletRequest@6731fda3
        log.info("request={}", request);

        String itemName = request.getParameter("itemName");
        // itemName=dd
        log.info("itemName={}", itemName);

        // multipart/form-data 전송 방식에서 나누어진 부분을 받아서 확인 가능.
        Collection<Part> parts = request.getParts();
        // parts=[org.apache.catalina.core.ApplicationPart@3ee8c9aa, org.apache.catalina.core.ApplicationPart@171a7f08]
        // 로그를 보면 parts에 2개가 들어와있는 걸 확인할 수 있다.
        log.info("parts={}", parts);

        return "upload-form";

        /**
         * (application.properties)
         * logging.level.org.apache.coyote.http11=debug
         * : HTTP 요청 메시지 확인 용도
         *
         * 일부분)
         * Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryaPx3p6TPU8hA9gTY
         * -> multipart/form-data 방식으로 전송된 것을 확인 가능.
         *
         * Content-Disposition: form-data; name="itemName"
         * -> 이런 식으로 구분도 되어 있다. (처음 입력한 상품명)
         *
         * Content-Disposition: form-data; name="file"; filename="íë¡ì í¸ ìíê³íì ë° ë³´ê³ ì ìì (2022.03.1x).hwp"
         * Content-Type: application/octet-stream
         * -> 그리고 업로드한 파일도 확인이 가능하다.
         *
         */

        /**
         * cf) 멀티파트 사용 옵션
         * - 업로드 사이즈 제한 -
         * spring.servlet.multipart.max-file-size=1MB (파일 하나의 최대 사이즈 제한)
         * spring.servlet.multipart.max-request-size=10MB (여러 파일 사이즈 합 제한)
         *
         * - 멀티파트 관련 옵션 처리 막기 -
         * spring.servlet.multipart.enabled=false
         * : false 설정 시 request log를 찍었을 때
         * HttpServletRequest 객체가 기본형인 RequestFacade형이 나온다.
         *
         * 디폴트 값은 true이다.
         * : 옵션을 켜면 request가 StandardMultipartHttpServletRequest로 변해서
         * 서블릿 컨테이너가 멀티파트와 관련된 처리를 하게 된다.
         * -> 이때, 디스패처 서블릿에서 multipartResolver를 실행하는데,
         * 이러면 HttpServletRequest를 MultipartHttpServeltRequest로 변환시켜 반환해준다.
         */
    }
}
