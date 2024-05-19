- @Value 사용하기
  - 기본값을 사용하기 위해서 : 뒤에 값을 붙여주면 된다.
  - `@Value("${my.datasource.etc.max-connection:1}") -> 기본값으로 1 설정
  
- @ConfigurationProperties를 하나하나 등록할 때는 @EnableConfigurationProperties를 사용한다.
  - 기본 방법은 자바빈 프로퍼티 방식이기 때문에 Getter, Setter가 필요하다.
  - 생성자를 활용하면 Getter만 사용하도록 만들 수 있다.
    - 스프링 부트 3.0 이전에서는 생성자 바인딩 시 @ConstructorBinding을 필수로 사용해야 했으나, 3.0부터는 생성자가 1개라면 생략이 가능해졌다.
    - 물론 생성자가 2개 이상인 경우에는 여전히 사용해야 한다.
  - 타입 보장이 가능하기 때문에 좀 더 안전하다
  - 또한, 자바 객체이기 때문에 @Validated를 사용하여 값에 대한 검증을 진행할 수 있다.
    - jakarta.validation.constraints 패키지의 기능을 사용하면 모두 자바 표준 기능들이다.

- @ConfigurationProperties 특정한 범위로 자동 등록할 때는 @ConfigurationPropertiesScan을 사용한다. 
- ex. @ConfigurationPropertiesScan({ "com.example.app", "com.example.another" })

- 스프링에서의 사용 예시
```java
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceAutoConfiguration {
}

@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties implements BeanClassLoaderAware, InitializingBean {
    private String name;
    // ...
}
```

- 거의 같이 사용하지 않겠지만, application.properties 와 application.yml 을 같이 사용하면 properties 파일이 우선권을 가진다.

---

### @Profile 어노테이션 원리
```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ProfileCondition.class)
public @interface Profile {

	String[] value();
}

/**
 * {@link Condition} that matches based on the value of a {@link Profile @Profile}
 * annotation.
 *
 * @author Chris Beams
 * @author Phillip Webb
 * @author Juergen Hoeller
 * @since 4.0
 */
class ProfileCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(Profile.class.getName());
        if (attrs != null) {
            for (Object value : attrs.get("value")) {
                if (context.getEnvironment().acceptsProfiles(Profiles.of((String[]) value))) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

}
```
- 딱히 고민을 안 해봤었는데 Condition 인터페이스를 활용해서 만들어졌던 거였다... 신기!
