package hello.springtx.order;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="orders") // order by 때문에 이름 바꿔줘야 함
@NoArgsConstructor
@Getter
@Setter
public class Order {
    @Id @GeneratedValue
    private Long id;

    private String username;
    private String payStatus;

}
