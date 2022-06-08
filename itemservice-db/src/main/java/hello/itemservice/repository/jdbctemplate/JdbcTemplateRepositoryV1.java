package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JdbcTemplate
 */
@Repository
@Slf4j
public class JdbcTemplateRepositoryV1 implements ItemRepository {

    // jdbc 템플릿 사용하기
    private final JdbcTemplate template;

    public JdbcTemplateRepositoryV1(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        String sql = "insert into item (item_name, price, quantity) values (?, ?, ?)";

        // id를 넣어줘야 하니까, db에서 생성해준 id를 가져오기. 이때 keyholder 사용
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // 데이터를 변경할 때는 template.update를 사용한다. (insert, update, delete 시 사용)
        // 반환값은 int형으로, 영향받은 row 수를 리턴한다.
        template.update(connection -> {
            // insert문 실행 이후에 db에 생성된 자동 증가 id 값을 조회할 수 있다.
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, item.getItemName());
            ps.setInt(2, item.getPrice());
            ps.setInt(3, item.getQuantity());
            return ps;
        }, keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        // ?에 바인딩할 파라미터를 순서대로 전달해준다.
        String sql = "update item set item_name=?, price=?, quantity=? where id=?";
        template.update(sql,
                updateParam.getItemName(),
                updateParam.getPrice(),
                updateParam.getQuantity(),
                itemId);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id = ?";
        try {
            // 데이터 단건 조회 시 사용한다.
            Item item = template.queryForObject(sql, itemRowMapper(), id);
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            // 결과가 없을 때는 예외 발생
            // 둘 이상일 경우에는 IncorrectResultSizeDataAccessException
            // 결과가 없을 때 Optional 반환하기 때문에, 결과가 없으면 empty() 대신 반환해주기
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        String sql = "select id, item_name, price, quantity from item";

        // 동적 쿼리
        if(StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }

        boolean andFlag = false;
        List<Object> param = new ArrayList<>();

        // itemName이 있으면
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',?,'%')";
            param.add(itemName);
            andFlag = true;
        }

        // maxPrice가 null이 아니면
        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= ?";
            param.add(maxPrice);
        }

        log.info("sql={}", sql);
        // 결과가 하나 이상일 때는 .query()를 사용한다
        // 결과가 없을 때는 빈 컬렉션을 반환해준다.
        return template.query(sql, itemRowMapper(), param.toArray());
    }

    private RowMapper<Item> itemRowMapper() {
        // RowMapper는 db의 반환 결과인 resultSet(rs)를 객체로 변환해준다
        // JdbcTemplate이 알아서 resultset이 끝날 때까지 rowMapper를 반복 진행해준다고 생각하면 될 듯! (커서 이동하는 것도 알아서 해줌!)
        return (rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setItemName(rs.getString("item_name"));
            item.setPrice(rs.getInt("price"));
            item.setQuantity(rs.getInt("quantity"));
            return item;
        };
    }
}
