package jpabook.jpashop2.domain;

import jpabook.jpashop2.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue
    @Column(name="category_id")
    private Long id;

    private String name;

    // 아이템과 카테고리는 다대다 관계.
    // 다대다여서 중간 테이블이 필요함 (사실 실무에서는 안 좋음)
    @ManyToMany
    @JoinTable(name="category_item",
            joinColumns = @JoinColumn(name="category_id"),
            inverseJoinColumns = @JoinColumn(name="item_id"))
    private List<Item> items = new ArrayList<>();

    // 자기 참조 타입
    // 내 부모 타입이니까 N:1
    // :1으로 끝나는 건 디폴트가 즉시 로딩이기 때문에 지연 로딩으로 반드시 바꿔줘야 한다.
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    // 연관관계 편의 메서드
    // parent-category(1) <-> child(n)
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }
}
