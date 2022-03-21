package hello.upload.controller;

import hello.upload.domain.Item;
import hello.upload.domain.ItemRepository;
import hello.upload.domain.UploadFile;
import hello.upload.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    // 등록 폼을 보여줌
    @GetMapping("/items/new")
    public String newItem(@ModelAttribute ItemForm form) {
        return "item-form";
    }

    // 폼의 데이터를 저장하고 보여주는 화면으로 리다이렉트
    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute ItemForm form,
                           RedirectAttributes redirectAttributes) throws IOException {

        // 넘어온 form에서 attachFile 꺼내오기 (첨부파일 1개)
        MultipartFile attachFile = form.getAttachFile();
        // 업로드 파일로 반환해주기
        UploadFile uploadFile = fileStore.storeFile(attachFile);
        // 또한, 다중 이미지 업로드를 가져와서 마찬가지로 저장해준다.
        List<MultipartFile> imageFiles = form.getImageFiles();
        List<UploadFile> uploadFiles = fileStore.storeFiles(imageFiles);

        // DB 저장 - 이때 보통 파일 자체를 저장한다기보단, 파일의 경로를 보통 저장한다고 한다.
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(uploadFile);
        item.setImageFiles(uploadFiles);
        itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", item.getId());

        return "redirect:/items/{itemId}";
    }

    // 상품 보여줌
    @GetMapping("/items/{id}")
    public String items (@PathVariable Long id, Model model) {
        // item 찾아서 보여주기
        Item item = itemRepository.findById(id);
        model.addAttribute("item", item);
        return "item-view";
    }

    // <img> 태그로 이미지를 조회할 때 사용
    // UrlResource로 이미지를 읽어서 @ResponseBody로 이미지 바이너리 반환함
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        // 파일명이 file:/Users/../pewo234-23-r23-r-223r2.png 뭐 이런 식으로 있을 텐데,
        // 그냥 그 url을 찾아와서 해당 경로에 있는 파일을 스트림으로 반환하는 것 같다.
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    // 파일 다운로드시 실행.
    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach (@PathVariable Long itemId) throws MalformedURLException {
        // itemId를 가져와서 (item을 접근할 수 있는 사용자만 다운받을 수 있도록 하기 위한...?)
        Item item = itemRepository.findById(itemId);

        // 업로드 파일명이랑 서버 저장명 가져오기
        String storeFileName = item.getAttachFile().getStoreFileName();
        String uploadFileName = item.getAttachFile().getUploadFileName();

        // 보통 파일 다운로드 시에 고객이 업로드한 이름으로 다운로드하는 게 좋으니까 파일 이름 가져오기
        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));
        log.info("uploadFileName={}", uploadFileName);

        // 업로드한 파일을 다운로드하기 위해서.
        // 인코딩된 파일명을 넣어줘야 한글이 안 깨짐
        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        // 여기서 header를 가져오는 걸 볼 수 있는데, 이는 파일 다운로드를 위한 것.
        // 안 넣으면 파일의 내용이 보여져버림.
        return ResponseEntity.ok()
                // 헤더의 값으로 attachment; filename="업로드한 파일명"을 주었다.
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}


