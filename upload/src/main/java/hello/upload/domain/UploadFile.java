package hello.upload.domain;

import lombok.Data;

// 업로드 파일 정보 보관
@Data
public class UploadFile {
    // 고객이 업로드한 파일명
    private String uploadFileName;
    // 서버 내부에서 관리하는 파일명 (파일명이 중복되지 않게 하기 위해서)
    private String storeFileName;

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
