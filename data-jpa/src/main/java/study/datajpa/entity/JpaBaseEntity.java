package study.datajpa.entity;

/*
    엔티티 생성, 변경 시 변경한 사람과 시간을 추적하고 싶다면?
    -> 등록일, 수정일, 등록자, 수정자
 */

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

// 진짜 상속 관계는 아니고, 속성만 내려서 사용할 수 있도록 하는 느낌
@MappedSuperclass
@Getter
public class JpaBaseEntity {

    // 값 변경 불가능하도록
    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    // persist 전에 발생하도록
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    // 업데이트 전에 발생하도록
    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }

}
