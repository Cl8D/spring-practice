package jpabook.jpashop.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name="PARENT_ID")
    private Category parent;

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
