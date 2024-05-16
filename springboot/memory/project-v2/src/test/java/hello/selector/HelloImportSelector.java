package hello.selector;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class HelloImportSelector implements ImportSelector {

    /**
     * 여기서 반환된 문자열 정보를 바탕으로 빈으로 등록한다.
     */
    @Override
    public String[] selectImports(final AnnotationMetadata importingClassMetadata) {
        return new String[]{"hello.selector.HelloConfig"};
    }
}
