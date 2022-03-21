package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/spring")
public class SpringUploadController {

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    // spring에서 지원하는 어노테이션을 활용하면 훨씬 더 간편하게 업로드가 가능하다.
    @PostMapping("/upload")
    public String saveFile (@RequestParam String itemName,
                            // 업로드하는 html form name에 맞춰서 @RequestParam을 적용해주면 된다.
                            // cf) ModelAttribute에서도 multipartFIle 적용 가능.
                            @RequestParam MultipartFile file,
                            HttpServletRequest request) throws IOException {

        log.info("request={}", request);
        log.info("itemName={}", itemName);
        log.info("multipartFile={}", file);

        if(!file.isEmpty()) {
            // 업로드 파일 명을 간단하게 가져올 수 있다.
            String fullPath = fileDir + file.getOriginalFilename();
            log.info("파일 저장 fullPath={}", fullPath);
            // 파일 저장
            file.transferTo(new File(fullPath));
        }

        return "upload-form";
    }
}
