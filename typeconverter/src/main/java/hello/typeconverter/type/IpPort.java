package hello.typeconverter.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;

// 127/0.0.1:8080 같은 IP, Port형 받기
@Getter
// equals(), hashcode()를 자동으로 만들어준다.
// 모든 필드의 값이 같아지면 a==b 성립.
@EqualsAndHashCode
public class IpPort {
    private String ip;
    private int port;

    public IpPort(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}
