package hello.typeconverter.converter;

import hello.typeconverter.type.IpPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class StringToIpPortConverter implements Converter<String, IpPort> {
    @Override
    public IpPort convert(String source) {
        log.info("convert source={}", source);
        // 127.0.0.1:8080
        String[] split = source.split(":");
        // 127.0.0.1에 해당
        String ip = split[0];
        int port = Integer.parseInt(split[1]);

        // string 형을 ipPort 형 객체로 변환하여 리턴
        return new IpPort(ip, port);
    }
}
