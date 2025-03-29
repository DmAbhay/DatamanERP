package dataman.erp.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMast {

    private Integer code;
    private String username;
    private String email;
    private String mobile;
    private String password;

}
