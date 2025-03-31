package dataman.erp.auth.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class Util {

    @Autowired
    @Qualifier("tirangaCompanyNamedJdbcTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Map<String, String> getUserCredentials(String username, String loginMode) {


        if(loginMode.equals("MOBILE")) {

            String sql = "SELECT user_Name, passWd FROM userMast WHERE mobile = :mobile";
            MapSqlParameterSource params = new MapSqlParameterSource().addValue("mobile", username);
            return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> Map.of(
                    "user_Name", rs.getString("user_Name"),
                    "passWd", rs.getString("passWd")
            ));

        }else if(loginMode.equals("EMAIL")){

            String sql = "SELECT user_Name, passWd FROM userMast WHERE eMail = :email";
            MapSqlParameterSource params = new MapSqlParameterSource().addValue("email", username);
            return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> Map.of(
                    "user_Name", rs.getString("user_Name"),
                    "passWd", rs.getString("passWd")
            ));

        }

        return null;
    }
}
