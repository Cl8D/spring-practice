package hellojpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@Entity
public class Team extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name="TEAM_ID")
    private Long id;

    private String name;

    // 양방향 연관관계
    // 팀의 입장에서는 멤버 여러 명이 가능하니까 1:N
    // mappedBy는 Member에서 연관이 되어 있는 애의 필드명을 가져오는 것.
    // 즉, 우리는 member class의 private Team team;과 연관!
    // 또한, mappedBy로 설정했기 때문에 team이 곧 member의 주인이 되는 것.
    // 얘는 값을 읽기만 가능하다.
    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    /*
    // 양방향 연관관계 주입. 이런 식으로도 할 수 있음.
    // 무한루프가 걸릴 수 있기 때문에 changeTeam이나 addMember 중 하나만 쓰자.
    public void addMember(Member member) {
        member.setTeam(this);
        members.add(member);
    }
    */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}
