package hello.hellospring.domain;

import javax.persistence.*;

// JPA가 관리하는 entity임을 의미함
@Entity
public class Member {

    // db에서 알아서 id 값을 지정해주는 것
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    /*
    // db의 column name이 username임을 의미한다 -
    @Column(name="username")
    private String name;
    */

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
