package hello.hellospring.controller;

public class MemberForm {
    // members 폴더에 있는 createMemberForm.html의 name과 일치
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
