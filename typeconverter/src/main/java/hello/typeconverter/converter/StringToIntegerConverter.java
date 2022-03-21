package hello.typeconverter.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
// String -> Integer
public class StringToIntegerConverter implements Converter<String, Integer> {
    @Override
    // source => String.
    public Integer convert(String source) {
        log.info("convert source={}", source);
        // 여기서 Integer.valueOf를 통해 integer형으로 바꿔줌.
        return Integer.valueOf(source);
    }

    // cf) 참고로, 얘를 등록하지 않아도
    // 스프링은 내부에서 기본 컨버터를 제공하기 때문에 @requestParam에서는 잘 변경된다.
    // 단, 추가한 컨버터가 기본 컨버터보다 우선순위가 높기 때문에 먼저 적용된다.
}
