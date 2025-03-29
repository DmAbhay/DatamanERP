package dataman.erp.auth.repository;

import dataman.erp.auth.dto.UserMast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserMastRepository {

    @Autowired
    @Qualifier("tirangaCompanyJdbcTemplate")
    private JdbcTemplate jdbcTemplate;


    private final RowMapper<UserMast> userRowMapper = (rs, rowNum) -> {
        UserMast user = new UserMast();
        user.setCode(Integer.valueOf(rs.getString("code")));
        user.setUsername(rs.getString("user_Name"));
        user.setEmail(rs.getString("eMail"));
        user.setPassword(rs.getString("passWd"));
        return user;
    };

    public Optional<UserMast> findByUsername(String username) {
        System.out.println("username is came here successfully in repository");
        String sql = "SELECT * FROM userMast WHERE user_Name = ?";
        try {
            UserMast user = jdbcTemplate.queryForObject(sql, userRowMapper, username);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty(); // Handle cases where no user is found
        }
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM userMast WHERE user_Name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }


}
