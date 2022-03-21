package hello.upload.file;

import hello.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// 파일 저장
@Component
public class FileStore {
    @Value("${file.dir}")
    private String fileDir;

    // 파일의 전체 경로를 얻는다 (지정된 경로 + 파일 이름)
    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    // 이미지 여러 개를 업로드할 수 있도록
    public List<UploadFile> storeFiles (List<MultipartFile> multipartFiles) throws IOException {
        // 이미지를 담는 리스트
        List<UploadFile> storeFileResult = new ArrayList<>();

        // multipartFile을 탐색하기
        for(MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty())
                // storeFile을 통해 생성된 uploadFile을 리스트에 담아주기
                storeFileResult.add(storeFile(multipartFile));
        }
        return storeFileResult;
    }

    // multipartFile을 받아서 uploadFile로 변환해주기
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty())
            return null;

        // 사용자가 업로드한 오리지널 파일 이름 가져오기
        String originalFilename = multipartFile.getOriginalFilename();
        // 서버에 저장할 파일명 지정
        String storeFileName = createStoreFileName(originalFilename);
        // 파일을 지정한 경로에 저장해준다.
        multipartFile.transferTo(new File(getFullPath(storeFileName)));
        // 그리고 업로드파일을 반환하는데, 이때 원본파일 이름과 서버 저장 파일 이름을 반환해준다.
        return new UploadFile(originalFilename, storeFileName);
    }

    // 서버에 저장할 때는 랜덤UUID + 확장자 형태로 저장하기
    // ex) 23r23-123-32r-243.png 이런 식으로 되도록.
    private String createStoreFileName(String originalFilename) {
        // 오리지널 파일명에서 확장자 꺼내오기
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        // UUID + 확장자 형태
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        // 원본 파일의 이름은 image.png 형태일 테니까
        // .의 위치를 알아내기
        int pos = originalFilename.lastIndexOf(".");
        // 그리고 . 다음 위치가 바로 확장자명이 된다.
        return originalFilename.substring(pos + 1);
    }
}
