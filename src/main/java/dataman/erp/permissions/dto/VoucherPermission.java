package dataman.erp.permissions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherPermission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String saveDate;
    private String viewDate;
    private String deleteDate;
    private Double allowValueUpto;
    private String voucherType;
    private String category;

}

