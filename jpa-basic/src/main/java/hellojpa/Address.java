package hellojpa;

import javax.persistence.Embeddable;

@Embeddable
public class Address {
    private String City;
    private String street;
    private String zipcode;

    public Address() {
        ;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
