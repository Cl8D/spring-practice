package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
// jpa는 꼭 기본 생성자가 필요함 (프록시 객체 생성 이런 이유 때문에)
// 사용자가 기본 생성자 사용하는 걸 막으면서, jpa에게 제공하도록 @NoArgsConstructor 사용하기
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 가급적이면 연관관계가 없는 내부 필드만 사용해주기
// 여기서 team까지는 X!! 그러면 team에서도 member 찍으려고 하면서 무한루프 걸림
@ToString(of = {"id", "username", "age"})
public class Member {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;
    private String username;
    private int age;

    // member(n) - team(1)
    // 연관관계의 주인
    @ManyToOne(fetch = FetchType.LAZY)
    // fk 설정... 이라고 봐도 될 듯
    @JoinColumn(name="team_id")
    private Team team;

    public Member(String username) {
        this(username, 0);
    }

    public Member(String username, int age){
        this(username, age, null);
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null)
            changeTeam(team);
    }

    // 양방향 연관관계 처리하기
    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }
}
