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
public class CompanyDetailDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("comp_Code")
    private Integer comp_Code;

    @JsonProperty("comp_Name")
    private String comp_Name;

    @JsonProperty("v_Prefix")
    private Integer v_Prefix;

    @JsonProperty("centralData_Path")
    private String centralData_Path;

    @JsonProperty("centralFileData_Path")
    private String centralFileData_Path;

    @JsonProperty("start_Dt")
    private String start_Dt;

    @JsonProperty("end_Dt")
    private String end_Dt;

    @JsonProperty("cyear")
    private String cyear;

}
