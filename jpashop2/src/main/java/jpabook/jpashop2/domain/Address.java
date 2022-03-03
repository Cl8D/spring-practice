package jpabook.jpashop2.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

// 값 타입 -> 기본적으로 변경이 되면 안 된다.
@Embeddable
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    // 값 타입의 경우 기본 생성자가 필요하다.
    // public에 비해 그나마 더 안전한 protected로 만들어주기.
    // JPA 구현 라이브러리가 객체 생성 시 리플렉션 같은 기술을 사용할 수 있도록 하는 것.
    protected Address() {

    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
