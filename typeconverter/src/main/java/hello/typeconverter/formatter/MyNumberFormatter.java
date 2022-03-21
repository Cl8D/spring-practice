package hello.typeconverter.formatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

@Slf4j
public class MyNumberFormatter implements Formatter<Number> {
    @Override
    // 문자 -> 객체
    public Number parse(String text, Locale locale) throws ParseException {
        log.info("text={}, locale={}", text, locale);
        // numberFormat은 locale 정보를 활용하여 나라별로 다른 숫자 포맷을 만들어준다.
        NumberFormat format = NumberFormat.getInstance(locale);
        // parse는 문자를 숫자로 변환한다.
        return format.parse(text);
    }

    @Override
    public String print(Number object, Locale locale) {
        log.info("object={}, locale={}", object, locale);
        // 객체 -> 문자
        return NumberFormat.getInstance(locale).format(object);
    }
}
