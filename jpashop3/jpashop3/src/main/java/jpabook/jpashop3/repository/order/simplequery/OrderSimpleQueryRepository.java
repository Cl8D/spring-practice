package jpabook.jpashop3.repository.order.simplequery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

// 조회 전용 리파지토리
@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        // new 명령어를 사용해서 JPQL의 결과를 DTO로 즉시 변환할 수 있다.
        // select를 통해서 원하는 데이터만 직접 선택하여 가져올 수 있다.
        // 엔티티를 바로 넘길 수 없기 때문에 o.id... 이런 식으로 넣어서 넘겨줘야 한다!
        String query = "select new"
                    + " jpabook.jpashop3.repository.order.simplequery.OrderSimpleQueryDto"
                    + "(o.id, m.name, o.orderDate, o.status, d.address)"
                    + " from Order o"
                    + " join o.member m"
                    + " join o.delivery d";

        // 두 번째 인자는 엔티티 클래스!
        return em.createQuery(query, OrderSimpleQueryDto.class)
                .getResultList();


    }



}
