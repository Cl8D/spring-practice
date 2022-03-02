package jpabook.jpashop2;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

// Entity -> JPA에서 정의된 필드를 바탕으로 db에 테이블을 생성해준다.
@Entity
@Getter @Setter
public class Member {
    // Id -> PK임을 지정
    // GeneratedValue -> PK가 자동으로 1씩 증가하는 형태로 지정
    @Id @GeneratedValue
    private Long id;
    private String username;



}
