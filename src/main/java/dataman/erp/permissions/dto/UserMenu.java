package dataman.erp.permissions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMenu implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String moduleName;
    private String refModuleName;
    private String menuText;
    private String mnuName;
    private String toolTip;
    private String permission;
    private String saveDate;
    private String viewDate;
    private String deleteDate;
    private Double allowValueUpto;
    private List<String> controlPermission = new ArrayList<>();
    private List<UserMenu> userMenuList = new ArrayList<>();

    public UserMenu(String menuText) {
        this.menuText = menuText;
    }

    public UserMenu(String menuText, String refModuleName) {
        this.menuText = menuText;
        this.refModuleName = refModuleName;
    }

    public UserMenu(String menuText, List<UserMenu> menuList) {
        this.menuText = menuText;
        this.userMenuList = menuList;
    }

    public void addControlPermission(String caption) {
        controlPermission.add(caption);
    }

    public UserMenu clone() {
        UserMenu userMenu = new UserMenu();
        userMenu.moduleName = this.moduleName;
        userMenu.refModuleName = this.refModuleName;
        userMenu.menuText = this.menuText;
        userMenu.deleteDate = this.deleteDate;
        userMenu.permission = this.permission;
        userMenu.saveDate = this.saveDate;
        userMenu.toolTip = this.toolTip;
        userMenu.viewDate = this.viewDate;

        if (this.controlPermission != null) {
            userMenu.controlPermission = new ArrayList<>(this.controlPermission);
        }

        if (this.userMenuList != null) {
            List<UserMenu> clonedList = new ArrayList<>();
            for (UserMenu um : this.userMenuList) {
                clonedList.add(um.clone());
            }
            userMenu.userMenuList = clonedList;
        }

        return userMenu;
    }
}

