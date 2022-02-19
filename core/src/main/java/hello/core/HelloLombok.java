package hello.core;

import lombok.Getter;
import lombok.Setter;


// lombok을 사용하면 getter, setter를 자동으로 만들어준다.
@Getter
@Setter
public class HelloLombok {
    private String name;
    private int age;

    public static void main(String[] args) {
        HelloLombok helloLombok = new HelloLombok();
        helloLombok.setName("hello");

        String name = helloLombok.getName();
        System.out.println("name = " + name);
    }

}
