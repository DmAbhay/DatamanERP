package dataman.erp.dmbase.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PLT {

    private String pltCode;
    private String pltName;
    private String userLinkedAccountCode;
    private String userLinkedAccountName;

}
