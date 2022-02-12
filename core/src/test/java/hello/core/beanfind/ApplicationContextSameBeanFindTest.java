package hello.core.beanfind;

import hello.core.AppConfig;
import hello.core.discount.DiscountPolicy;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationContextSameBeanFindTest {
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SameBeanConfig.class);

    @Test
    @DisplayName("타입으로 조회 시 같은 타입이 둘 이상이라면, 중복 오류가 발생한다")
    // org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type 'hello.core.member.MemberRepository' available: expected single matching bean but found 2: memberRepository1,memberRepository2
    // 두 개가 찾아졌다는 에러가 뜬다.
    void findBeanByTypeDuplicate() {
        // 꺼내는 애의 타입을 지정했는데,
        // 이때 MemberRepository가 타입인 애가 2개가 존재해버린다. (memberRepository1, 2)
        //MemberRepository bean = ac.getBean(MemberRepository.class);
        assertThrows(NoUniqueBeanDefinitionException.class,
                () -> ac.getBean(MemberRepository.class));
    }

    @Test
    @DisplayName("타입으로 조회 시 같은 타입이 둘 이상 있으면, 빈 이름을 지정하면 된다")
    void findBeanByName() {
        // 이런 식으로 이름을 지정해준다.
        MemberRepository memberRepository = ac.getBean("memberRepository1", MemberRepository.class);
        assertThat(memberRepository).isInstanceOf(MemberRepository.class);
    }

    @Test
    @DisplayName("특정 타입을 모두 조회하기")
    void findAllBeanByType() {
        // 모두 조회할 때는 key-value 값으로, Map 형태로 조회가 가능하다
        /*
        출력 결과)
        key = memberRepository1 value = hello.core.member.MemoryMemberRepository@52d645b1
        key = memberRepository2 value = hello.core.member.MemoryMemberRepository@2101b44a
        beansOfType = {memberRepository1=hello.core.member.MemoryMemberRepository@52d645b1, memberRepository2=hello.core.member.MemoryMemberRepository@2101b44a}
        */
        Map<String, MemberRepository> beansOfType = ac.getBeansOfType(MemberRepository.class);
        for (String key : beansOfType.keySet()) {
            System.out.println("key = " + key + " value = " + beansOfType.get(key));
        }
        System.out.println("beansOfType = " + beansOfType);

        // 검색된 타입이 2개인지
        assertThat(beansOfType.size()).isEqualTo(2);
    }


    // 같은 타입이 둘 이상인 걸 만들기 위해 임시로 만든 AppConfig
    @Configuration
    // static으로 지정 시에 메서드의 scope가 해당 클래스 내로 좁혀진다.
    static class SameBeanConfig {
        @Bean
        public MemberRepository memberRepository1() {
            return new MemoryMemberRepository();
        }
        @Bean
        public MemberRepository memberRepository2() {
            return new MemoryMemberRepository();
        }

    }
}
