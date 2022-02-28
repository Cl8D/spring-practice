package hellojpa;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// JPA가 사용한다는 걸 알려주기 위해서 @entity 필요함!
@Entity
// db에 저장된 table 이름이 다를 때는 이런 식으로 설정해주기. 보통 클래스 이름을 따른다.
//@Table(name = "USER")
//public class Member extends BaseEntity {
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

    /*
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
    */


    // 단방향 연관관계
    // 참조 대신에 외래 키를 그대로 사용
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name= "USERNAME")
    private String username;


    // 임베디드 타입
    @Embedded
    private Period workPeriod;

    @Embedded
    private Address homeAddress;

    // 같은 타입을 2번 사용하고 싶을 때
    //@Embedded
    //@AttributeOverrides(여기에 원래 값을 써야 하는데 귀찮으니 생략)
    //private Address workAddress;

    //@Column(name = "TEAM_ID")
    //private Long teamId;

    /*
    // 하나의 팀에 여러 멤버가 들어올 수 있기 때문에,
    // Member 클래스의 입장에서는 ManyToOne을 설정해줘야 한다.
    @ManyToOne
    // 그리고 fk값과 매핑하기 위해
    // db에 값을 변경할 때는 연관관계의 주인이 얘이기 때문에 얘로만 참조를 진행한다.
    @JoinColumn(name="TEAM_ID")
    private Team team;
    */


    /*
    // 일대다 양방향 매핑. 읽기 전용 매핑이 생기는 것.
    // fetch -> 지연 로딩 LAZY
    // 이러면 team은 proxy 객체를 조회하고, Member만 db에서 조회하게 된다.
    //@ManyToOne(fetch = FetchType.LAZY)
    // 즉시 로딩 사용 -> Member-Team을 함께 조회할 때
    @ManyToOne(fetch = FetchType.EAGER)
    //@JoinColumn(name="TEAM_ID", insertable = false, updatable = false)
    @JoinColumn
    private Team team;
    */

    /*
    // 일대일 주 테이블 외래키 단방향
    @OneToOne
    @JoinColumn(name="LOCKER_ID")
    private Locker locker;
    */

    /*
    // 다대다 관계
    @ManyToMany
    // 연결 테이블 지정하기
    @JoinTable(name="MEMBER_PRODUCT")
    private List<Product> products = new ArrayList<>();
    */

    /*
    // 다대다 -> 일대다, 다대일로 변경
    @OneToMany(mappedBy = "member")
    private List<MemberProduct> memberProducts = new ArrayList<>();
    */

    /*
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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
    */

    /*
    // 조금 더 중요한 함수임을 알려주기 위해 getter-setter 관례에서 벗어나도록
    public void changeTeam(Team team) {
        this.team = team;

        // 연관관계 편의 메소드.
        // main 대신에 여기서 member 자기 자신을 넣어준다.
        // 양방향 연관관계를 주입해주기.
        team.getMembers().add(this);
    }
    */
}

