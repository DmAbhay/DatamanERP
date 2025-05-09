package dataman.erp.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dataman.dmbase.debug.Debug;
import dataman.dmbase.redissessionutil.RedisObjectUtil;
import dataman.dmbase.redissessionutil.RedisSimpleKeyValuePairUtil;
import dataman.dmbase.redissessionutil.SessionUtil;
import dataman.dmbase.utils.DmUtil;
import dataman.erp.auth.dto.AuthRequestDTO;
import dataman.erp.auth.dto.LoginResponseDTO;

import dataman.erp.auth.util.Util;
import dataman.erp.context.PCSDataStore;
import dataman.erp.dmbase.models.CompanyDetailDTO;
import dataman.erp.dmbase.models.PCSData;
import dataman.erp.dmbase.models.PLTDetailDTO;
import dataman.erp.auth.service.UserMastService;
import dataman.erp.config.ExternalConfig;
import dataman.erp.dmbase.models.SiteDetailsDTO;
import dataman.erp.dmbase.service.DmBaseService;
import dataman.erp.jwt.JwtTokenUtil;
import dataman.erp.mapper.PCSDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class UserMastController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserMastService userMastService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisObjectUtil redisObjectUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisSimpleKeyValuePairUtil redisSimpleKeyValuePairUtil;

    @Autowired
    private ExternalConfig externalConfig;

    @Autowired
    private DmBaseService dmBaseService;

    @Autowired
    private PCSDataMapper pcsDataMapper;

    @Autowired
    private PCSDataStore pcsDataStore;

    @Autowired
    private Util util;

    @Autowired
    private SessionUtil sessionUtil;



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO authRequest) {


        try {

            String loginMode = DmUtil.getLoginMode(authRequest.getUserName());
            System.out.println(loginMode);
            System.out.println(authRequest);
            //System.out.println(LoginMode.getByCode(1));

            if(!loginMode.equals("USERNAME")){
                //String loginMode = LoginMode.getByCode(Integer.parseInt(authRequest.getLoginMode()));


                System.out.println(loginMode);
                Map<String, String> userCredentials = util.getUserCredentials(authRequest.getUserName(), loginMode);

                if(userCredentials != null){

                    authRequest.setUserName(userCredentials.get("user_Name"));
                    //authRequest.setPassword(userCredentials.get("passWd"));

                }else{
                    throw new BadCredentialsException("Invalid email or password");
                }
            }


            System.out.println(authRequest);


            // Authenticate the user
            System.out.println(authRequest.getUserName());
            System.out.println(authRequest.getPassword());

//            if(Integer.parseInt(authRequest.getLoginMode()) == 3){
//                System.out.println("Come as for execution");
//                authenticationManager.authenticate(
//                        new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
//                );
//            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword())
            );


        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        System.out.println("User Validated!!!!!!!!!!!!!!!!!!");

        PCSData pcsData = new PCSData();
        pcsData.setUserName(authRequest.getUserName());
        pcsData.setLoginDate(String.valueOf(authRequest.getLoginDate()));
        pcsData.setUserDescription(authRequest.getUserDescription());
        pcsData.setRegionalLanguage(authRequest.getRegionalLanguage());



        // Fetch user details
        UserDetails userDetails = userMastService.loadUserByUsername(authRequest.getUserName());

        // Generate JWT token
        String token = jwtTokenUtil.generateToken(userDetails.getUsername());

        // Optionally, store the token in a cache (e.g., Redis) with the associated username for further validation

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setAuthKey(null);
        loginResponseDTO.setToken(token);
        loginResponseDTO.setSecretKey(null);

        pcsData.setUserName(authRequest.getUserName().toLowerCase());

        HashMap<String, String> hm = new HashMap<>();

        hm.put("token", token);
        String companyDB = externalConfig.getCompanyDb();

//        hm.put("companyDB", companyDB);
//        hm.put("username", authRequest.getUserName());


        System.out.println(hm);
        String key = authRequest.getUserName().toLowerCase() + "_"+ token;
        redisObjectUtil.saveObject(key, hm, 60, TimeUnit.MINUTES);

//        redisObjectUtil.addFieldToObject(key, "address", "Buxar, Bihar");
//        redisObjectUtil.addFieldToObject(key, "username", authRequest.getUserName());
//        redisObjectUtil.addFieldToObject(key, "loginDate", authRequest.getLoginDate());

        String loginDate = DmUtil.convertUnixTimestampToFormattedDate(authRequest.getLoginDate());
        sessionUtil.setSsnUserName(key, authRequest.getUserName());
        sessionUtil.setSsnLoginDate(key, loginDate);
        sessionUtil.setSsnDBName(key, companyDB);



        //redisObjectUtil.addFieldToObject(key, "userDescription", authRequest.getUserDescription());
        //redisObjectUtil.addFieldToObject(key, "regionalLanguage", authRequest.getRegionalLanguage());

        Debug.printDebugBoundary();
        System.out.println(redisObjectUtil.getObjectValue(key, "address"));
        Debug.printDebugBoundary();

        // Create a new ObjectNode
        ObjectNode responseNode = objectMapper.createObjectNode();

        // Add loginResponseDTO using putPOJO
        responseNode.putPOJO("loginResponse", loginResponseDTO);

//==========================Get List of companies and plts==========================================

        List<PLTDetailDTO> listOfPlts = pcsDataMapper.mapToPLTDetailDTOList(dmBaseService.getPlts(authRequest.getUserName()));

        if((listOfPlts != null) && (listOfPlts.size() == 1)){
            int selectedPltCode = listOfPlts.get(0).getPLTCode();
            String compName = listOfPlts.get(0).getComp_Name();
            redisObjectUtil.addFieldToObject(key, "selectedPlt", listOfPlts.get(0));
            sessionUtil.setSsnPltCode(key, String.valueOf(selectedPltCode));
            sessionUtil.setSsnCompName(key, compName);
            pcsData.setPltSelected(listOfPlts.get(0));
            List<CompanyDetailDTO> listOfCompany = pcsDataMapper.mapToCompanyDetailDTOList(dmBaseService.getCompanys(authRequest.getUserName(), selectedPltCode, companyDB));
            if((listOfCompany != null) && (listOfCompany.size() == 1)){
                //if(true){
                pcsData.setCSelected(listOfCompany.get(0));
                String selectedCompCode = String.valueOf(listOfCompany.get(0).getComp_Code());

                sessionUtil.setSsnCompCode(key, selectedCompCode);
                redisObjectUtil.addFieldToObject(key, "selectedCompany", listOfCompany.get(0));
                //List<Map<String, Object>> listOfSites = dmBaseService.getSites(authRequest.getUsername(), externalConfig.getCompanyDb(), selectedCompCode, String.valueOf(selectedPltCode));
                List<SiteDetailsDTO> listOfSites = pcsDataMapper.mapToSiteDetailsDTOList(dmBaseService.getSites(authRequest.getUserName(), externalConfig.getCompanyDb(), selectedCompCode, String.valueOf(selectedPltCode)));

                if(listOfSites != null && listOfSites.size() == 1){
                    String selectedSiteCode  = "1234";// use me in permission query
                    pcsData.setSSelected(listOfSites.get(0));
                    String regionalLanguage = listOfSites.get(0).getRegionalLanguage();
                    redisObjectUtil.addFieldToObject(key, "selectedSites", listOfSites.get(0));

                    sessionUtil.setSsnSiteCode(key, selectedSiteCode);
                    sessionUtil.setSsnRegionalLanguage(key, regionalLanguage);
                    redisObjectUtil.addFieldToObject(key, "regionalLanguage", listOfSites.get(0).getRegionalLanguage());
                    ResponseEntity.ok("GO TO PERMISSION PAGE");
                }else{
                    pcsData.setLstSites(listOfSites);
                }
            }else{
                //responseNode.putPOJO("listOfCompany", listOfCompany);
                pcsData.setLstCompanys(listOfCompany);
                //return ResponseEntity.ok(listOfCompany);
            }
        }else{
            //responseNode.putPOJO("listOfPlts", listOfPlts);
            pcsData.setLstPLTs(listOfPlts);
            //return ResponseEntity.ok(listOfPlts);
        }

        responseNode.putPOJO("pcsData", pcsData);
        return ResponseEntity.ok(responseNode);

    }
}
