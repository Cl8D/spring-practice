package hellojpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

// 값 타입 컬렉션의 대안
@Entity
@Table(name="ADDRESS")
public class AddressEntity {
    @Id
    @GeneratedValue
    private Long id;

    // 값 타입 넣어주기
    private Address address;

    public AddressEntity(){
        ;
    }

    public AddressEntity(Long id, Address address) {
        this.id = id;
        this.address = address;
    }

    public AddressEntity(String city, String street, String zipcode) {
        this.address = new Address(city, street, zipcode);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
