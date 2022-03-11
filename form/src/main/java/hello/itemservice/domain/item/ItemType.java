package hello.itemservice.domain.item;

public enum ItemType {
    BOOK("도서"), FOOD("식품"), ETC("기타");

    // 설명을 위해서 description 필드 추가
    private final String description;

    ItemType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
