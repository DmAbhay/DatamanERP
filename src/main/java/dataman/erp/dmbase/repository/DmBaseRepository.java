package dataman.erp.dmbase.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class DmBaseRepository {

    @Autowired
    @Qualifier("tirangaCompanyJdbcTemplate")
    private JdbcTemplate jdbcTemplate;



    @Autowired
    @Qualifier("tirangaCompanyNamedJdbcTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;



    public List<Map<String, Object>> getPLTDetailForSuparAndSA() {
        String sql = """
            SELECT PLT.PLTCode, MAX(PLT.Comp_Name) AS Comp_Name, NULL AS subCode
            FROM ProductLicensedTo PLT
            LEFT JOIN Company C ON C.PLTCode = PLT.PLTCode
            LEFT JOIN UserSite US ON US.CompCode = C.Comp_Code
            GROUP BY PLT.PLTCode
        """;


        System.out.println(jdbcTemplate.queryForList(sql));

        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getPLTDetailsForUser(String userName) {
        String sql = """
            SELECT PLT.PLTCode, MAX(PLT.Comp_Name) AS Comp_Name, MAX(ULA.subCode) AS subCode
            FROM ProductLicensedTo PLT
            LEFT JOIN Company C ON C.PLTCode = PLT.PLTCode
            LEFT JOIN UserSite US ON US.CompCode = C.Comp_Code
            LEFT JOIN userLinkAccount ULA ON ULA.pltCode = PLT.PLTCode AND ULA.user_Name = ?
            WHERE US.User_Name = ?
            GROUP BY PLT.PLTCode
        """;

        return jdbcTemplate.queryForList(sql, userName, userName);
    }

    public List<Map<String, Object>> getCompanysForSuperAndSA(int pltCode, String dbCompany) {
        // Define the SQL query
        String sql = "SELECT C.Comp_Code, " +
                "MAX(p.Comp_Name) AS Comp_Name, " +
                "MAX(C.V_Prefix) AS V_Prefix, " +
                "MAX(CentralData_Path) AS CentralData_Path, " +
                "MAX(centralFileData_Path) AS centralFileData_Path, " +
                "MAX(C.Start_Dt) AS Start_Dt, " +
                "MAX(C.End_Dt) AS End_Dt, " +
                "MAX(C.CYear) AS CYear " +
                "FROM Company C " +
                "LEFT JOIN " + dbCompany.trim() + ".dbo.productLicensedTo p ON C.pltCode = p.pltCode " +
                "LEFT JOIN UserSite US ON US.CompCode = C.Comp_Code " +
                "WHERE C.PLTCode = :pltCode " +
                "GROUP BY C.Comp_Code, C.Start_Dt " +
                "ORDER BY C.Start_Dt DESC;";

        // Create SQL parameters
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("pltCode", pltCode);

        // Execute query and return result
        return namedParameterJdbcTemplate.queryForList(sql, params);
    }

    public List<Map<String, Object>> getCompanysForUser(int pltCode, String dbCompany, String userName) {
        String sqlQuery = "SELECT C.Comp_Code, MAX(p.Comp_Name) AS Comp_Name, MAX(C.V_Prefix) AS V_Prefix, " +
                "MAX(CentralData_Path) AS CentralData_Path, MAX(centralFileData_Path) AS centralFileData_Path, " +
                "MAX(C.Start_Dt) AS Start_Dt, MAX(C.End_Dt) AS End_Dt, MAX(C.CYear) AS CYear " +
                "FROM Company C " +
                "LEFT JOIN " + dbCompany.trim() + ".dbo.productLicensedTo p ON C.pltCode = p.pltCode " +
                "LEFT JOIN UserSite US ON US.CompCode = C.Comp_Code " +
                "WHERE US.User_Name = :user_Name AND C.PLTCode = :pltCode " +
                "GROUP BY C.Comp_Code, C.Start_Dt " +
                "ORDER BY C.Start_Dt DESC;";

        // Creating named parameters
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_Name", userName)
                .addValue("pltCode", pltCode);

        // Executing query and returning result as a list of maps
        return namedParameterJdbcTemplate.queryForList(sqlQuery, parameters);
    }


    public List<Map<String, Object>> getSites(String dbCompany, String userName, String compCode, String pltCode) {


        String userSiteList = fetchUserSiteList(userName, dbCompany, compCode);


        String sqlQuery = "SELECT sm.Code, sm.Name, sm.regionalLanguage, sm.manualCode, sm.stateNumericCode, "
                + "bm.name AS branchName, sm.serialKey, sm.clientCode, sm.Address1, sm.Address2, sm.City, "
                + "sm.Pin, sm.mobile, sm.phone, sm.eMail, sm.drugLicenseNo, sm.gstin, sm.gstinDate, sm.siteDisplayName, "
                + "sm.panNo, sm.tan, sm.stateName, sm.country, sm.gstCountryCode "
                + "FROM SiteMast sm "
                + "LEFT JOIN " + dbCompany.trim() + ".dbo.branchMast bm ON bm.code = sm.branchCode "
                + "WHERE sm.Code IN (:userSiteList) AND sm.pltCode = :pltCode AND bm.code IS NOT NULL";

        Map<String, Object> params = new HashMap<>();
        params.put("userSiteList", Arrays.asList(userSiteList.split(","))); // Convert to a list
        params.put("pltCode", pltCode);

        return namedParameterJdbcTemplate.queryForList(sqlQuery, params);
    }


    public String fetchUserSiteList(String userName, String dbCompany, String compCode) {
        List<String> userSiteList = new ArrayList<>();

        if (userName.trim().equalsIgnoreCase("SA") || userName.trim().equalsIgnoreCase("SUPER")) {
            // Fetch all site codes for super users
            System.out.println("user is SA or SUPER");
            String sqlQuery = "SELECT SM.Code FROM SiteMast SM";
            userSiteList = namedParameterJdbcTemplate.queryForList(sqlQuery, new HashMap<>(), String.class);
            System.out.println(userSiteList);
        } else {
                String sqlQuery = "SELECT US.SiteList FROM " + dbCompany + ".dbo.UserSite US "
                        + "WHERE US.User_Name = :userName AND US.CompCode = :compCode";

                Map<String, Object> params = new HashMap<>();
                params.put("userName", userName);
                params.put("compCode", compCode);

                System.out.println("\uD83D\uDD34");

                // Execute the query to fetch data
                List<String> siteLists = namedParameterJdbcTemplate.queryForList(sqlQuery, params, String.class);


            for (int j = 0; j < siteLists.size(); j++) {
                String temp = siteLists.get(j).trim().replace("|", ""); // Use replace() instead of replaceAll()
                userSiteList.add(temp);
            }

        }

        String result = userSiteList.get(0);

        for(int i = 1;i<userSiteList.size();i++){
            String temp = userSiteList.get(i);
            result = result + "," + temp;
        }

        return result;
    }

}
