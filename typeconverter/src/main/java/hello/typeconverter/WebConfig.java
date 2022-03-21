package hello.typeconverter;

import hello.typeconverter.converter.IntegerToStringConverter;
import hello.typeconverter.converter.IpPortToStringConverter;
import hello.typeconverter.converter.StringToIntegerConverter;
import hello.typeconverter.converter.StringToIpPortConverter;
import hello.typeconverter.formatter.MyNumberFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // registry -> 등록에 초점을 둠.
        // 스프링은 내부에서 ConversionService를 제공하기 때문에,
        // WebMvcConfigurer가 제공하는 addFormatter()를 사용해서 추가하고 싶은 컨버터를 등록한다.
        // 이러면 스프링이 알아서 ConversionService에 컨버터를 추가해준다.
        //registry.addConverter(new StringToIntegerConverter());
        //registry.addConverter(new IntegerToStringConverter());
        registry.addConverter(new StringToIpPortConverter());
        registry.addConverter(new IpPortToStringConverter());

        // formatter가 숫자 -> 문자, 문자-> 숫자도 변경하기 때문에
        // 위에 등록했던 converter 2개는 제거해주자. (중복 처리)
        // 참고로, 컨버터가 우선순위가 더 높다.
        registry.addFormatter(new MyNumberFormatter());
    }
}
