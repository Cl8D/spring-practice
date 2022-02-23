package hellojpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

// JPA가 사용한다는 걸 알려주기 위해서 @entity 필요함!
@Entity
// db에 저장된 table 이름이 다를 때는 이런 식으로 설정해주기. 보통 클래스 이름을 따른다.
//@Table(name = "USER")
public class Member {

    // JPA에게 PK를 알려주기 위해서 @Id 사용하기
    @Id
    private Long id;

    // 마찬가지로 컬럼 이름이 다를 때도 이런 식으로 설정 가능
    //@Column(name="username")
    private String name;

    // JPA는 기본적으로 내부에서 동적으로 객체를 생성해야 하기 때문에, 기본 생성자가 필요하다.
    public Member() {

    }

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
}
