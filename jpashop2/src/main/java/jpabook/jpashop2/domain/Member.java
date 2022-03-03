package jpabook.jpashop2.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// Entity -> JPA에서 정의된 필드를 바탕으로 db에 테이블을 생성해준다.
@Entity
@Getter @Setter
public class Member {
    // Id -> PK임을 지정
    // GeneratedValue -> PK가 자동으로 1씩 증가하는 형태로 지정
    @Id
    @GeneratedValue
    @Column(name="member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    // 주인이 아닌 애에게 mappedBy로 주인을 설정해준다.
    // Order table에 존재하는 member로 인해 매핑되었음을 의미한다.
    // 주인으로 설정된 애의 값을 바꾸게 되면 fk 값이 변경된다.
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();


}
