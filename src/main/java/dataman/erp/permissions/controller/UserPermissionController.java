package dataman.erp.permissions.controller;

import dataman.dmbase.redissessionutil.RedisObjectUtil;
import dataman.erp.jwt.JwtTokenUtil;
import dataman.erp.permissions.dto.UserMenu;
import dataman.erp.permissions.dto.UserPermission;
import dataman.erp.permissions.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserPermissionController {


    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisObjectUtil redisObjectUtil;

    @Autowired
    private UserPermissionService userPermissionService;

    @PostMapping("/get-permission")
    public ResponseEntity<?> getUserPermission(@RequestHeader(value = "Authorization", required = true) String token){

        String uName = null;
        if (token.startsWith("Bearer ")) {
            uName = jwtTokenUtil.extractUsername(token.substring(7)).toLowerCase();
            System.out.println(uName);
        }

//        List<UserMenu> userMenus = userPermissionService.getUserPermissions("SA", "5", "Dashboard");
//        System.out.println(userMenus);

        String key = uName + "_" + token.substring(7);
        String db = redisObjectUtil.getObjectValueAsString(key, "companyDB");

        UserPermission userPermission = userPermissionService.getUserPermission(key, "");

        return ResponseEntity.ok(userPermission);

    }

}
