package dataman.erp.permissions.service;


import dataman.dmbase.debug.Debug;
import dataman.dmbase.redissessionutil.RedisObjectUtil;
import dataman.dmbase.redissessionutil.SessionUtil;
import dataman.erp.permissions.dto.UserMenu;
import dataman.erp.permissions.dto.UserPermission;
import dataman.erp.permissions.dto.VoucherPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserPermissionService {

    @Autowired
    @Qualifier("tirangaCompanyNamedJdbcTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Autowired
    private SessionUtil sessionUtil;

    @Autowired
    private RedisObjectUtil redisObjectUtil;


    public UserPermission getUserPermission(String key, String moduleAllowed) {
        LinkedHashMap<String, List<UserMenu>> userPermission = new LinkedHashMap<>();
        List<VoucherPermission> voucherPermission = new ArrayList<>();

        String userName = sessionUtil.getSsnUserName(key);
        String compCode = sessionUtil.getSsnCompCode(key);
        Debug.printDebugBoundary("\uD83D\uDC8B\uD83D\uDC8B\uD83D\uDC8B\uD83D\uDC8B");
        System.out.println("company code "+compCode);
        Debug.printDebugBoundary("\uD83D\uDC8B\uD83D\uDC8B\uD83D\uDC8B\uD83D\uDC8B");
        String dbName = sessionUtil.getSsnTransactionDBName(key);

        String db = redisObjectUtil.getObjectValueAsString(key, "companyDB");

        if (compCode == null || compCode.trim().equals("0")) {
            throw new RuntimeException("Invalid company.");
        }

        String condition = "";
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        params.put("compCode", compCode);

        if (moduleAllowed != null && !moduleAllowed.trim().isEmpty()) {
            condition = " AND upw.mnuModule IN (:moduleAllowed) ";
            List<String> moduleList = Arrays.stream(moduleAllowed.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
            params.put("moduleAllowed", moduleList);
        }

        String sqlQuery = "SELECT upw.mnuModule, MIN(upw.sno) AS sno " +
                "FROM user_permission_web upw " +
                "WHERE upw.isActive = 1 " +
                "AND upw.userName = :userName " +
                "AND ISNULL(upw.Comp_Code, -1) IN (:compCode, '-1') " +
                condition +
                "GROUP BY upw.mnuModule " +
                "ORDER BY MIN(upw.sno)";

        List<String> moduleNames = namedParameterJdbcTemplate.query(sqlQuery, params, (rs, rowNum) -> rs.getString("mnuModule"));

        for (String module : moduleNames) {
            List<UserMenu> menuList = new ArrayList<>();
            getUserMenu(userName, compCode, module, menuList, "", 0, namedParameterJdbcTemplate);
            userPermission.put(module.toLowerCase(), menuList);
        }

        // Voucher Permissions
        String currentDate = LocalDate.now().toString(); // Format: yyyy-MM-dd
        String voucherSql = "SELECT uvp.Category, vt.V_Type, " +
                "CASE WHEN ISNULL(uvp.VP_Days, 0) = 0 THEN NULL ELSE DATEADD(DAY, -(uvp.VP_Days - 1), :currentDate) END AS VP_Date, " +
                "CASE WHEN ISNULL(uvp.DP_Days, 0) = 0 THEN NULL ELSE DATEADD(DAY, -(uvp.DP_Days - 1), :currentDate) END AS DP_Date, " +
                "CASE WHEN ISNULL(uvp.SP_days, 0) = 0 THEN NULL ELSE DATEADD(DAY, -(uvp.SP_days - 1), :currentDate) END AS SP_Date, " +
                "uvp.AllowValueUpto, uvp.isAllowAudit AS AllowAudit " +
                "FROM User_Voucher_Permission uvp " +
                "LEFT JOIN " + dbName + ".dbo.voucher_Type vt ON vt.short_Name = uvp.vTypeSName " +
                "WHERE uvp.User_Name = :userName AND uvp.Comp_Code = :compCode";

        params.put("currentDate", currentDate);

        namedParameterJdbcTemplate.query(voucherSql, params, rs -> {
            voucherPermission.add(new VoucherPermission(
                    rs.getDate("SP_Date") != null ? rs.getDate("SP_Date").toString() : null,
                    rs.getDate("VP_Date") != null ? rs.getDate("VP_Date").toString() : null,
                    rs.getDate("DP_Date") != null ? rs.getDate("DP_Date").toString() : null,
                    rs.getDouble("AllowValueUpto"),
                    rs.getString("V_Type"),
                    rs.getString("Category")
            ));
        });

        return new UserPermission(userPermission, voucherPermission);
    }


    private void getUserMenu(
            String userName,
            String compCode,
            String moduleName,
            List<UserMenu> menuList,
            String parentMenu,
            int level,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate) {

        System.out.println("🔄 getUserMenu() CALLED for user: " + userName + ", module: " + moduleName + ", parent: " + parentMenu + ", level: " + level);

        // Fetch menus for this level and parentMenu
        List<UserMenu> currentLevelMenus = getUserPermissions(userName, compCode, moduleName, parentMenu, namedParameterJdbcTemplate);

        // If no child menus, stop recursion
        if (currentLevelMenus == null || currentLevelMenus.isEmpty()) {
            System.out.println("⛔ No child menus for parent: " + parentMenu);
            return;
        }

        // Process each menu and recurse to get submenus
        for (UserMenu userMenu : currentLevelMenus) {
            System.out.println("✅ Found Menu: " + userMenu.getMenuText());

            // Recursively fetch and set child menus
            List<UserMenu> subMenus = new ArrayList<>();
            getUserMenu(
                    userName,
                    compCode,
                    moduleName,
                    subMenus,                  // Submenu list for this item
                    userMenu.getMnuName(),     // Use this menu as parent for next level
                    level + 1,
                    namedParameterJdbcTemplate
            );
            userMenu.setUserMenuList(subMenus); // Attach children to current menu

            // Add this menu to the result list
            menuList.add(userMenu);
        }

        System.out.println("✅ Completed level " + level + " for parent: " + parentMenu);
    }


    public List<UserMenu> getUserPermissions(
            String userName,
            String compCode,
            String moduleName,
            String parentMenu,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate) {

        String sqlQuery = "SELECT UPW.SNo, UPW_S.parent, ucpw.mnuName AS controlMnuName, UPW.refModule, UPW.MnuModule, " +
                "UPW.MnuName, UPW.MnuText, UPW.MnuLevel, UPW.ReportFor, UPW.ReportFor AS DisplayModuleName, " +
                "UPW.permission, " +
                "CASE WHEN ISNULL(UPW.VP_Days, 0) = 0 THEN NULL ELSE DATEADD(day, -(UPW.VP_Days - 1), :currentDate) END AS VP_Date, " +
                "CASE WHEN ISNULL(UPW.DP_Days, 0) = 0 THEN NULL ELSE DATEADD(day, -(UPW.DP_Days - 1), :currentDate) END AS DP_Date, " +
                "CASE WHEN ISNULL(UPW.SP_days, 0) = 0 THEN NULL ELSE DATEADD(day, -(UPW.SP_days - 1), :currentDate) END AS SP_Date, " +
                "UPW.AllowValueUpto, mc.mnuShortHand " +
                "FROM User_permission_web UPW " +
                "LEFT JOIN ( " +
                "    SELECT mnuModule, parent FROM User_permission_web " +
                "    WHERE isActive = 1 AND UserName = :userName AND ISNULL(Comp_Code, '-1') IN (:compCodes) " +
                "    GROUP BY mnuModule, parent " +
                ") AS UPW_S ON UPW_S.parent = UPW.MnuName AND UPW_S.mnuModule = UPW.mnuModule " +
                "LEFT JOIN ( " +
                "    SELECT mnuModule, mnuName FROM user_control_permission_web " +
                "    WHERE userName = :userName AND ISNULL(Comp_Code, '-1') IN (:compCodes) " +
                "    GROUP BY mnuModule, mnuName " +
                ") AS ucpw ON ucpw.mnuName = UPW.MnuName AND ucpw.mnuModule = UPW.mnuModule " +
                "LEFT JOIN menuCodification mc ON mc.mnuModule = UPW.mnuModule AND mc.mnuName = UPW.mnuName " +
                "WHERE UPW.isActive = 1 " +
                "AND UPW.UserName = :userName " +
                "AND UPW.mnuModule = :moduleName " +
                "AND ISNULL(UPW.Comp_Code, '-1') IN (:compCodes) " +
                "AND ISNULL(LTRIM(RTRIM(UPW.parent)), '') = ISNULL(LTRIM(RTRIM(:parentMenu)), '') " +
                "ORDER BY UPW.sno";

        String currentDate = LocalDate.now().toString(); // Use today's date

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userName", userName);
        params.addValue("compCodes", List.of(compCode, "-1"));
        params.addValue("moduleName", moduleName);
        params.addValue("parentMenu", parentMenu);
        params.addValue("currentDate", currentDate);

        return namedParameterJdbcTemplate.query(sqlQuery, params, (rs, rowNum) -> {
            UserMenu userMenu = new UserMenu(rs.getString("MnuText"));
            userMenu.setModuleName(rs.getString("MnuModule"));
            userMenu.setRefModuleName(rs.getString("refModule"));
            userMenu.setPermission(rs.getString("permission"));
            userMenu.setMnuName(rs.getString("MnuName"));
            userMenu.setToolTip(rs.getString("mnuShortHand"));
            userMenu.setViewDate(String.valueOf(rs.getDate("VP_Date")));
            userMenu.setSaveDate(String.valueOf(rs.getDate("SP_Date")));
            userMenu.setDeleteDate(String.valueOf(rs.getDate("DP_Date")));
            return userMenu;
        });
    }


}
