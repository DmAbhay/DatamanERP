package dataman.erp.permissions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPermission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private LinkedHashMap<String, List<UserMenu>> userMenuList;
    private List<VoucherPermission> voucherPermissionList;
    private String voucherType;
    private String voucherDate;
    private String voucherValue;


    public UserPermission(LinkedHashMap<String, List<UserMenu>> userMenuList,
                          List<VoucherPermission> voucherPermissionList
                          ) {
        this.userMenuList = userMenuList;
        this.voucherPermissionList = voucherPermissionList;

    }
}

