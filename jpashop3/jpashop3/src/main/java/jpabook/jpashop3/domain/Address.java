package jpabook.jpashop3.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

// 값 타입 -> 변경 불가능하도록
// 값은 생성자로부터 초기화 가능.
@Embeddable
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() {

    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
