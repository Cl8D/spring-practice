package jpabook.jpashop3.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id @GeneratedValue
    @Column(name="member_id")
    private long id;

    // 값이 비어있으면 x
    @NotEmpty
    private String name;

    @Embedded
    private Address address;

    // member(1) - order(n)
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

}
