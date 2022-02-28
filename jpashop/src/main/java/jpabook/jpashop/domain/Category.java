package jpabook.jpashop.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
public class Category extends BaseEntity{

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    // ManyToOne의 default fetch는 eager이기 때문에 지연 로딩으로 변경해주기.
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="PARENT_ID")
    private Category parent;

    // 반대로 이 친구는 기본이 lazy여서 ㄱㅊ
    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    // item과 n:m
    @ManyToMany
    // 다대다에서 중간 테이블 설계
    // 본인이 조인하는 건 처음 거, 조인되는 반대편의 것은 두 번째 거
    @JoinTable(name="CATEGORY_ITEM",
    joinColumns = @JoinColumn(name="CATEGORY_ID"),
    inverseJoinColumns = @JoinColumn(name="ITEM_ID"))
    private List<Item> items = new ArrayList<>();

}
