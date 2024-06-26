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
---

### 프로메테우스 / 그라파나
- 애플리케이션에서 발생한 메트릭을 과거 이력까지 함께 학인하려면 어딘가에 저장해야 하는데, 프로메테우스는 이러한 메트릭에 대해 수집하는 역할을 담당한다. 일종의 DB.
- 그라파나는 프로메테우스에 저장된 메트릭을 사용자가 보기 편한 형태로 그래프를 통해 보여주는 툴이다.

- 전체적인 구조
  - 스프링 부트 액츄에이터와 마이크로미터는 수많은 메트릭을 자동으로 만든다.
    - 이때, 추상화된 마이크로미터에 대한 구현체는 프로메테우스가 읽을 수 있는 포맷으로 생성해준다.
  - 프로메테우스는 만들어진 메트릭을 지속적으로 수집한다.
  - 그리고, 수집한 메트릭을 내부 DB에 저장한다.
  - 사용자는 그라파나를 통해서 메트릭을 편리하게 조회한다.
- 더 자세한 건 공식 문서로... [문서](https://prometheus.io/docs/introduction/overview/)

- 필요한 설정
  - 애플리케이션 설정: 프로메테우스가 애플리케이션의 메트릭을 가져갈 수 있도록 애플리케이션에서 프로메테우스 포맷에 맞추어 메트릭 만들기
  - 프로메테우스 설정: 프로메테우스가 애플리케이션의 메트릭을 주기적으로 수집하도록 설정하기

---

### 프로메테우스
- 프로메테우스는 기본적으로 . 대신에 _ 포맷을 사용한다.
- 로그 수처럼 지속적으로 숫자가 증가하는 메트릭을 '카운터' 라고 하며, 카운트 메트릭은 항상 마지막에 _total 을 붙인다.
- 프로메테우스 설정 파일 (prometheus.yml) 을 수정하게 되면 메트릭을 주기적으로 수집하도록 설정할 수 있다.
```
- job_name: "spring-actuator"
  metrics_path: '/actuator/prometheus'
  scrape_interval: 1s
  static_configs:
- targets: ['localhost:8080']
```
- job_name: 프로메테우스가 수집하는 대상의 이름
- metrics_path: 수집할 메트릭의 경로
- scrape_interval: 수집 주기 (기본 값은 1m, 이것 역시 성능에 영향을 줄 수 있기 때문에 실무에서는 고려해서 설정하기)
- targets: 수집할 대상의 주소


### 프로메테우스 기본 기능 (http://localhost:9090/graph)
```
http_server_requests_seconds_count{error="none", exception="none", instance="localhost:8080", job="spring-actuator", method="GET", outcome="SUCCESS", status="200", uri="/actuator/prometheus"} 40
```
- 기본적인 검색 시 나오는 값들을 마이크로미터에서는 '태그(Tag)' 라고 하며, 프로메테우스에서는 '레이블(Label)' 이라고 한다.
  - 태그: error, exception, instance, job...
- 끝에 나오는 숫자가 바로 메트릭의 값이다.
  - 40 이라는 값


- 조회 방식
  - Table -> Evaluation Time을 수정하여 과거 시간에 대한 메트릭을 조회할 수 있음
  - Graph -> 메트릭을 그래프로 조회할 수 있음


- 필터링
  - {} 중괄호를 활용하여 필터링을 진행할 수 있다.
  - = : 제공된 문자열과 동일한 레이블 선택
  - != : 제공된 문자열과 다른 레이블 선택
  - =~ : 정규식과 일치하는 레이블 선택
  - !~ : 정규식과 일치하지 않는 레이블 선택


- 쿼리 함수
  - sum(metric_name) : 값의 합계를 구한다
  - sum by(a, b)(metric_name): SQL에서 제공하는 group by 와 비슷하다
  - count(metric_name): 메트릭 자체의 수에 대한 카운트 값
  - topk(n, metric_name) : 상위 n개의 메트릭 조회
  - metric_name offset 10m : 현재를 기준으로 10분 이전의 데이터를 반환
  - rate(metric_name[5m]) : 5분 간격으로 메트릭의 변화량을 반환
  - metric_name[1m]: 지난 1분 동안의 모든 기록값 선택. 차트에 바로 표현이 불가능하다.
  - increase(metric_name[1m]): 1분 동안의 증가량을 반환
  - irate(metric_name[1m]): 1분 동안의 증가율을 반환
  - delta(metric_name[1m]): 1분 동안의 변화량을 반환

### 게이지와 카운터
- MeterRegistry, 스프링을 통해서 주입받고 여기에서 카운터나 게이지를 등록할 수 있다.

- 게이지 (Gauge)
  - 임의로 오르내릴 수 있는 단일 숫자 값
  - 값의 현재 상태를 볼 때 사용하며, 증가하거나 감소가 가능하다.
    - 카운터의 경우 값이 감소가 불가능하지만, 게이지는 감소가 가능하다는 점이 큰 차이점.
  - CPU 사용량, 메모리 사용량, 사용 중인 커넥션

- 카운터 (Counter)
  - [문서](https://prometheus.io/docs/concepts/metric_types/#counter)
  - 단순하게 증가하는 단일 누적 값 (누적해서 증가하는 값)
    - 단일 값 그 자체
    - 하나씩 증가하는 값, 혹은 0으로 초기화
    - 누적된 값이기 때문에 전체 값을 포함한다
    - _total과 같은 suffix가 붙는 게 일반적
  - HTTP 요청 수, 에러 수, 배치 처리 수
  - 특정 시간에 얼마나 많은 요청이 들어왔는지 확인하기 위해 increase, rate 같은 함수를 지원한다.

- 타이머 (Timer)
  - 카운터와 유사하지만 시간을 측정할 수 있다. 타임 윈도우를 통해 1~3분마다 최대 실행 시간이 재계산된다.
  - seconds_count: 누적된 실행 수
  - seconds_sum: 실행 시간의 합계
  - seconds_max: 실행 시간의 최대값 (게이지 느낌)


- increase()
  - 지정한 시간 단위별로 증가하는 값 확인
  - ex. increase(http_server_requests_seconds_count{uri="/log"}[1m])
- rate()
  - 범위 벡터에서 고객의 요청이 얼마나 증가했는지 확인, 초당 얼마나 증가했는지.
  - increase()는 숫자를 카운트 한다면, rate()는 초당 평균을 나누어서 계산하게 된다.
  - ex. rate(data[1m]) -> 60을 나눈 값
- irate()
  - 범위 벡터에서 초당 순간 증가율을 계산. 급격하게 증가한 내용을 확인하기 좋음.
- 쿼리 관련 [공식 문서](https://prometheus.io/docs/prometheus/latest/querying/basics/)


---

### 모니터링 활용하기
- CPU 사용량 확인하기
- JVM 메모리 사용량 확인하기
  -> JVM Heap, JVM Non-Heap, JVM Buffer Pool, JVM GC 등
- JDBC Connection Pool 확인하기
  -> Connection Close가 되지 않고 계속 Active 상태로 남아있을 때
  -> Hikari CP Active, IDLE, Pending 상태 커넥션 확인하기 (Connection Timeout 확인)
  -> Long Query 등으로 인해 응답 시간이 너무 길어져서 커넥션 반환이 안 되어서 발생한다든지...
- 에러 로그 확인하기
  -> 에러 로그가 높아지면 현재 프로그램에 어떠한 문제가 있다는 것이니까
