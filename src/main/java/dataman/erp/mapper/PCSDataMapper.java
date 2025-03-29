package dataman.erp.mapper;

import dataman.erp.dmbase.models.CompanyDetailDTO;
import dataman.erp.dmbase.models.PLTDetailDTO;
import dataman.erp.dmbase.models.SiteDetailsDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PCSDataMapper {



    private Integer getIntValue(Object value) {
        return (value instanceof Number) ? ((Number) value).intValue() : null;
    }

    private String getStringValue(Object value) {
        return (value != null) ? value.toString() : null;
    }
    private LocalDate getLocalDateValue(Object value) {
        return value != null ? LocalDate.parse(value.toString()) : null;
    }



    // Helper method to convert List<Map<String, Object>> to List<PLTDetailDTO>
    public List<PLTDetailDTO> mapToPLTDetailDTOList(List<Map<String, Object>> dataList) {
        return dataList.stream()
                .map(map -> new PLTDetailDTO(
//                        ((Number) map.get("PLTCode")).intValue(),
                        getIntValue(map.get("PLTCode")),
                        getStringValue(map.get("Comp_Name")),
                        getIntValue(map.get("subCode"))
                ))
                .collect(Collectors.toList());
    }

    public List<CompanyDetailDTO> mapToCompanyDetailDTOList(List<Map<String, Object>> dataList) {
        return dataList.stream()
                .map(map -> new CompanyDetailDTO(
                        getIntValue(map.get("Comp_Code")),       // Handle null safely
                        getStringValue(map.get("Comp_Name")),
                        getIntValue(map.get("V_Prefix")),       // Handle null safely
                        getStringValue(map.get("CentralData_Path")),
                        getStringValue(map.get("centralFileData_Path")),
                        getStringValue(map.get("Start_Dt")),
                        getStringValue(map.get("End_Dt")),
                        getStringValue(map.get("CYear"))
                ))
                .collect(Collectors.toList());


    }

    public List<SiteDetailsDTO> mapToSiteDetailsDTOList(List<Map<String, Object>> dataList) {
        return dataList.stream()
                .map(map -> new SiteDetailsDTO(
                        getIntValue(map.get("Code")),
                        getStringValue(map.get("Name")),
                        getStringValue(map.get("regionalLanguage")),
                        getStringValue(map.get("manualCode")),
                        getIntValue(map.get("stateNumericCode")),
                        getStringValue(map.get("branchName")),
                        getStringValue(map.get("serialKey")),
                        getStringValue(map.get("clientCode")),
                        getStringValue(map.get("Address1")),
                        getStringValue(map.get("Address2")),
                        getStringValue(map.get("City")),
                        getStringValue(map.get("Pin")),
                        getStringValue(map.get("mobile")),
                        getStringValue(map.get("phone")),
                        getStringValue(map.get("eMail")),
                        getStringValue(map.get("drugLicenseNo")),
                        getStringValue(map.get("gstin")),
                        getLocalDateValue(map.get("gstinDate")),
                        getStringValue(map.get("siteDisplayName")),
                        getStringValue(map.get("panNo")),
                        getStringValue(map.get("tan")),
                        getStringValue(map.get("stateName")),
                        getStringValue(map.get("country")),
                        getStringValue(map.get("gstCountryCode"))
                ))
                .collect(Collectors.toList());
    }


}
