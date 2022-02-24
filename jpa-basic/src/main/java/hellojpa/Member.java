package hellojpa;

import javax.persistence.*;
import java.util.Date;

// JPA가 사용한다는 걸 알려주기 위해서 @entity 필요함!
@Entity
// db에 저장된 table 이름이 다를 때는 이런 식으로 설정해주기. 보통 클래스 이름을 따른다.
//@Table(name = "USER")
public class Member {

    /*
    // JPA에게 PK를 알려주기 위해서 @Id 사용하기
    @Id
    private Long id;

    // 마찬가지로 컬럼 이름이 다를 때도 이런 식으로 설정 가능
    //@Column(name="username")
    // 제약조건 추가 가능
    @Column(unique =true, length=10)
    private String name;
    */

    /*
    // 필드와 컬럼 매핑 예제
    @Id
    private Long id;

    // DB에는 name이라는 이름으로 주고 싶어서 설정
    @Column(name = "name")
    private String username;

    private Integer age;

    // enum 타입을 사용하고 싶다면, Enumerated 사용하기 (기본적으로 db에는 enum 타입이 x)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    // 날짜 타입을 위해 Temporal 사용
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    // DB에 varchar를 넘어서는 큰 content를 위해서는 Lob 사용
    @Lob
    private String description;
    */

     /*
    public Member(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    */

    // 기본 키 매핑
    // 내가 직접 세팅을 하고 싶다면 @Id
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="name", nullable = false)
    private String username;

    // JPA는 기본적으로 내부에서 동적으로 객체를 생성해야 하기 때문에, 기본 생성자가 필요하다.
    public Member() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

