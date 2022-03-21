package hello.upload.controller;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// 상품 저장용 폼
@Data
public class ItemForm {
    private Long itemId;
    private String itemName;
    // 이미지 다중 업로드를 위해 사용
    private List<MultipartFile> imageFiles;
    // 멀티파트는 @ModelAttribute에서 사용 가능
    private MultipartFile attachFile;
}
