package hello.login.domain.member;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class Member {
    private Long id;

    // 로그인 id
    @NotEmpty
    private String loginId;

    // 사용자 이름
    @NotEmpty
    private String name;

    @NotEmpty
    private String password;

}
