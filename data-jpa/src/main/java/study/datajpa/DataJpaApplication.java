package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

// @SpringBootApplication이 붙은 곳과 동일한 패키지 범위에 있는 애들은
// spring JPA가 알아서 인식할 수 있게 됨 (springboot가 해준다)
// @EnableJpaRepositories(basePackages = "jpabook.jpashop.repository")
@SpringBootApplication
// Spring Data JPA Auditing 활용
@EnableJpaAuditing
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	// 등록자, 수정자 처리.
	@Bean
	public AuditorAware<String> auditorProvider() {
		// 여기서는 그냥 uuid를 생성해서 넘겨줬는데,
		// 보통은 뭐 세션 정보나 spring security 로그인 정보로부터 id를 받는다.
		return() -> Optional.of(UUID.randomUUID().toString());
	}
}
