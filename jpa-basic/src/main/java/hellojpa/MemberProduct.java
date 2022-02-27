package hellojpa;

import javax.persistence.*;

@Entity
public class MemberProduct {
    @Id
    @GeneratedValue
    private Long id;

    // 다대다 -> 다대일, 일대다
    @ManyToOne
    @JoinColumn(name="MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name="PRODUCT_ID")
    private Product product;



}
