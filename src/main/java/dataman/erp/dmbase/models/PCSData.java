package dataman.erp.dmbase.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PCSData {

    private String loginDate = null;
    private String userDescription = null;
    private String userName = null;
    private String regionalLanguage = null;

    public List<PLTDetailDTO> lstPLTs = new ArrayList<>();
    public PLTDetailDTO pltSelected = null;

    public List<CompanyDetailDTO> lstCompanys = new ArrayList<>();
    public CompanyDetailDTO cSelected = null;

    public List<SiteDetailsDTO> lstSites = new ArrayList<>();

    @JsonProperty("sSelected")
    public SiteDetailsDTO sSelected = null;
}
