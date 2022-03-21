package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/servlet/v2")
public class ServletUploadControllerV2 {

    // application.properties에서 적용한 경로
    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFileV1 (HttpServletRequest request) throws ServletException, IOException {
        log.info("request={}", request);

        String itemName = request.getParameter("itemName");
        log.info("itemName={}", itemName);

        Collection<Part> parts = request.getParts();
        log.info("parts={}", parts);

        // 멀티 파트 형식에서는 전송 데이터를 각각의 part로 나누어서 전송한다.
        // 이렇게 나눈 part들을 모아둔 게 parts.
        for (Part part : parts) {
            log.info("==== PART ====");

            // part의 이름 출력하기
            ///  name=file
            log.info("name={}", part.getName());

            // part 역시 헤더-바디로 구분되기 때문에 part의 헤더 가져오기
            Collection<String> headerNames = part.getHeaderNames();
            for (String headerName : headerNames) {
                /// header content-disposition: form-data; name="file"; filename="cat.png"
                /// header content-type: image/png
                log.info("header {}: {}", headerName, part.getHeader(headerName));
            }

            // 편의 메서드 사용해보기
            // content-disposition에는 name, filename이 들어오는데
            // 이때 filename을 찾아오려는 것.
            /// submittedFileName=cat.png
            log.info("submittedFileName={}", part.getSubmittedFileName());
            // part의 바디 크기 뽑기
            /// size=34729
            log.info("size={}", part.getSize());

            // 데이터 읽기
            // part의 전송 데이터를 읽을 수 있다.
            InputStream inputStream = part.getInputStream();
            // 읽은 데이터를 string형으로. (UTF-8 형식으로 찍기)
            String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            //log.info("body={}", body);

            // 파일에 저장하기
            if(StringUtils.hasText(part.getSubmittedFileName())) {
                // 클라이언트가 전달한 파일명. 파일 경로를 덧붙여서 파일의 전체 경로를 가져온다.
                String fullPath = fileDir + part.getSubmittedFileName();
                /// 파일 저장 fullPath=C:/Users/ljwon/springcat.png
                log.info("파일 저장 fullPath={}", fullPath);

                // part를 통해 전송된 데이터 저장하기
                part.write(fullPath);
            }
        }

        return "upload-form";
    }
}
