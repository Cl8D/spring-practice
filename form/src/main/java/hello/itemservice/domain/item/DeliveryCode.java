package hello.itemservice.domain.item;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * FAST: 빠른 배송
 * NORMAL: 일반 배송
 * SLOW: 느린 배송
 */

@Data
@AllArgsConstructor
// 배송 방식
public class DeliveryCode {
    // code는 FAST처럼 시스템에서 전달하는 값
    private String code;
    // '빠른 배송' 같이 고객에게 보여주는 값
    private String displayName;
}
