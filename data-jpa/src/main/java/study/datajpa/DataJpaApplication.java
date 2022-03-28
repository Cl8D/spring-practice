package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication이 붙은 곳과 동일한 패키지 범위에 있는 애들은
// spring JPA가 알아서 인식할 수 있게 됨 (springboot가 해준다)
// @EnableJpaRepositories(basePackages = "jpabook.jpashop.repository")
@SpringBootApplication
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

}
