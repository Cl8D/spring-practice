### 설정 정보 주입받기
- OS 레벨의 환경변수를 읽어오는 방법
  - System.getenv()
- VM option 환경변수 읽어오는 방법 (자바 시스템 옵션)
  - System.getProperties
  - System.getProperty("key")
- 커맨드 라인 인수 읽어오기
  - args: Array<String> 
  - 애플리케이션 실행 시점에 외부 설정 값을 main 함수의 파라미터로 받아오는 방법
  - 단, 이 방법은 개발자가 직접 파싱을 해서 값을 얻어와야 한다
- 커맨드 라인 옵션 인수
  - 커맨드 라인은 띄어쓰기로 구분하기 때문에 파싱이 필요하다는 점에서 번거로움
  - 스프링 스펙 -> 커맨드 라인에 -- (dash 2개)로 연결해서 시작하면 key-value로 받을 수 있게 해줌
  - 하나의 키에 여러 개의 값을 지정할 수 있음
  - ex. --username=userA --username=userB
- 스프링 부트 제공 스펙: ApplicationArguments
  -  ApplicationArguments를 주입받아서 사용, 내부적으로 입력한 커맨드 라인을 관리해준다.
--> 이렇게 제공되고 있는 다양한 방법을 추상화 할 수 없을까?


### Environment, PropertySource
- 스프링에서 외부 설정값을 유연하게 읽어오기 위해 추상화한 방법
  - 외부 설정값에는 properties, yml 같은 파일도 추가된다.
- PropertySource 라는 추상 클래스를 제공하고, 각각의 외부 설정을 조회하는 구체 클래스들을 구현해두었다.
  - ex. SystemEnvironmentPropertySource
```java
public abstract class PropertySource<T> {
    public String getProperty(String name) {
        throw new UnsupportedOperationException(USAGE_ERROR);
    }
}
```
- 스프링은 로딩 시점에 필요한 PropertySource 들을 생성하고, Environment에서 사용할 수 있게 연결한다.

- Environment는 특정한 외부 설정에 종속되지 않고, key-value 값으로 일관되게 읽을 수 있게 만들어준다.
```java
public interface Environment extends PropertyResolver {
}
```
- Environment는 PropertyResolver를 상속받아서 구현하고 있기 때문에, getProperty() 메서드를 사용할 수 있다.
  - environment.getProperty(key)
- 외부 설정을 이제 모두 Environment를 통해서 읽을 수 있게 된다.

---

### 스프링 profile 설정
- spring.profiles.active=.=...
- -Dspring.profiles.active=.=...

- 혹은 yml 파일에서 #--- 이나 !---으로 구분해서 논리적으로 나누기
  - 요거 나누는 영역 위/아래에는 주석(#)을 달면 안 된다고 한다.
  - 그리고 spring.config.activate.on-profile=... 해당 영역을 프로필별로 활성화하기
  - 프로필은 한 번에 두 개 이살 설정도 가능하다. (spring.profiles.active=dev,prod)

- 프로퍼티 파일의 경우 위에서 아래로 읽으면서 사용하게 된다.
  - 그래서 동일한 조건이라면 동일한 키에 대해 아래에 있는 프로퍼티가 최종적으로 결정된다. 
  - [스프링 공식 문서](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)

- 처음 알았는데 jar 빌드 후 해당 파일 위치에 application.properties를 두어도 설정 정보 주입이 된다...!
  - 물론 jar 내부에 적용된 파일이 우선적으로 적용된다

---

### 우선순위 (아래로 갈수록 높음)
- 설정 데이터 (application.properties)
- OS 환경변수
- 자바 시스템 속성
- 커맨드 라인 옵션 인수
