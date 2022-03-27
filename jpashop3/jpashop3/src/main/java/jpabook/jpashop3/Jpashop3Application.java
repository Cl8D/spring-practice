package jpabook.jpashop3;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Jpashop3Application {

	public static void main(String[] args) {
		SpringApplication.run(Jpashop3Application.class, args);
	}

	/*
	@Bean
	Hibernate5Module hibernate5Module() {
		//강제 지연 로딩 설정 -> 양방향 연관관계에 @JsonIgnore 설정 필수. 아니면 무한루프.
		Hibernate5Module hibernate5Module = new Hibernate5Module();
		hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		return new Hibernate5Module();
		// 이렇게 해주면, 초기화된 프록시 객체만 노출해주며, 초기화되지 않은 프록시 객체를 노출하지 않는다.
	}
	*/

	/**
	 * 갑자기 기억 안 나서 써두는 지연 로딩.
	 * Member-Team이 있을 때, 만약 member를 em.find로 찾게 되면
	 * 바로 쿼리를 날려서 team의 정보까지 가져오는 것이 아니라,
	 * 일단 프록시 객체를 만들어둔 다음에
	 * 나중에 getTeam()을 통해 실제로 알고 싶을 때 진짜로 쿼리를 날려서
	 * 영속성 컨텍스트에 저장을 해두는 방법.
	 * (이렇게 영속성 컨텍스트에 한 번 저장되면 그뒤로 거기서 가져옴)
	 *
	 * 즉시로딩은 반면에 조인으로 되어 있으면 필요한 거 한 번에 다 가져오는 방법이다.
	 * 예상치못한 쿼리가 엄청 많이 나갈 수 있음!
	 *
	 */
}
