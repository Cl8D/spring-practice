### 엔드포인트 사용하기
- 엔드포인트 활성화하기 
- 엔드포인트 노출하기 (jmx or web)
-> 기본적으로 활성화는 되어 있으나, 노출은 되어 있지 않음 (예외적으로 shutdown은 활성화 X)

### 엔드포인트 목록
- `beans` : 스프링 빈 확인
- `conditions` : Condition을 통해 빈을 등록할 때 해당 조건과 일치하거나, 일치하지 않는 원인 표시
- `env` : Environment 정보를 보여줌.
- `loggers` : 애플리케이션 로거 설정, 변경도 가능함
- `health` : health checking
  - 애플리케이션이 사용하는 DB가 응답하는지, 디스크 사용량은 괜찮은지 등, 다양한 정보를 포함하여 알려준다.
- `httpexchanges` : HTTP 호출 응답 정보, HttpExchangeRepository 를 구현한 빈을 사용해야 함.
- `info` : 애플리케이션 정보를 보여줌.
  - 자바 런타임 정보나 OS 정보, 빌드 정보 등을 보여준다.
- `metrics` : 애플리케이션의 메트릭 정보를 보여줌.
- `threaddump` : thread dump
- `shutdown` : 애플리케이션 종료. 기본적으로 비활성화.
- 그 외 [공식 문서 참고하기](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints)

---

### 마이크로미터 (micrometer)
- 모니터링 툴이 정말 많으며, 모니터링 툴에 전달하는 데이터도 다 다르다. 이에 대해 추상화를 한 표준 측정 방식임.
- 애플리케이션의 메트릭을 마이크로미터가 정한 표준 방법으로 모아서 제공을 해주고 있으며, JMX나 프로메테우스 같은 구현체들로 쉽게 갈아끼울 수 있다.
- 스프링 부트 액츄에이터는 마이크로미터가 제공하는 지표 수집을 @AutoConfiuration을 통해서 자동으로 등록해준다.
  - /actuator/metrics 엔드포인트를 활용하면 확인 가능
- 태그 필터 (availableTags)를 통해 필터링도 가능하다.
  - tag=KEY:VALUE와 같이 사용하여 더 상세한 정보를 확인할 수 있다.
  - ex. 힙 메모리 확인
  - /actuator/metrics/jvm.memory.used?tag=area:heap
- 메트릭 관련 [공식 문서](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics.supported)
