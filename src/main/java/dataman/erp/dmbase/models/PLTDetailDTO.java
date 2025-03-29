package dataman.erp.dmbase.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PLTDetailDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("PLTCode")
    private Integer PLTCode;

    @JsonProperty("Comp_Name")
    private String Comp_Name;

    @JsonProperty("subCode")
    private Integer subCode;
}
