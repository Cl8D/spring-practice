package hello.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Member {
    private String memberId;
    private String name;

    public Member(final String memberId, final String name) {
        this.memberId = memberId;
        this.name = name;
    }
}
