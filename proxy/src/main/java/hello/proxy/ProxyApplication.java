package hello.proxy;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.v1_proxy.ConcreteProxyConfig;
import hello.proxy.config.v1_proxy.InterfaceProxyConfig;
import hello.proxy.trace.logtrace.LogTrace;
import hello.proxy.trace.logtrace.ThreadLocalLogTrace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

//@Import({AppV1Config.class, AppV2Config.class}) // 클래스를 스프링 빈으로 등록
//@Import(InterfaceProxyConfig.class)
@Import(ConcreteProxyConfig.class)
// 컴포넌트 스캔이랑 동일함 (@ComponentScan)
// 여기서 일부러 app과 하위 패키지만 컴포넌트 스캔 대상이 되도록 하였다 (Config 파일을 계속 변경해나가려고)
@SpringBootApplication(scanBasePackages = "hello.proxy.app") //주의
public class ProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

	// logTrace는 여기서 등록해줬다 (다른 config에서도 사용하려구)
	@Bean
	public LogTrace logTrace() {
		return new ThreadLocalLogTrace();
	}

}
