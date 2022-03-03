package jpabook.jpashop2.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter @Setter
// 각각ㅇ르 구분하는 값
@DiscriminatorValue("B")
public class Book extends Item{
    private String author;
    private String isbn;
}
