package dataman.erp.dmbase.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiteDetailsDTO {

    private Integer code;
    private String name;
    private String regionalLanguage;
    private String manualCode;
    private Integer stateNumericCode;
    private String branchName;
    private String serialKey;
    private String clientCode;
    private String address1;
    private String address2;
    private String city;
    private String pin;
    private String mobile;
    private String phone;
    private String email;
    private String drugLicenseNo;
    private String gstin;
    private LocalDate gstinDate;
    private String siteDisplayName;
    private String panNo;
    private String tan;
    private String stateName;
    private String country;
    private String gstCountryCode;
}
