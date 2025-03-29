package dataman.erp.dmbase.controller;

import dataman.dmbase.debug.Debug;
import dataman.dmbase.redissessionutil.RedisObjectUtil;
import dataman.erp.config.ExternalConfig;
import dataman.erp.context.PCSDataStore;
import dataman.erp.dmbase.models.CompanyDetailDTO;
import dataman.erp.dmbase.models.PCSData;
import dataman.erp.dmbase.models.PLTDetailDTO;
import dataman.erp.dmbase.models.SiteDetailsDTO;
import dataman.erp.dmbase.service.DmBaseService;
import dataman.erp.jwt.JwtTokenUtil;
import dataman.erp.mapper.PCSDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DmBaseController {

    @Autowired
    private DmBaseService dmBaseService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisObjectUtil redisObjectUtil;

    @Autowired
    private ExternalConfig externalConfig;

    @Autowired
    private PCSDataMapper pcsDataMapper;

    @Autowired
    private PCSDataStore pcsDataStore;

    @GetMapping("/get-plts")
    public ResponseEntity<?> getPlts(@RequestHeader(value = "Authorization", required = true) String token, @RequestParam String username){

        String uName = null;
        if (token.startsWith("Bearer ")) {
            uName = jwtTokenUtil.extractUsername(token.substring(7)).toLowerCase();
            System.out.println(uName);
        }

        String key = uName + "_" + token.substring(7);
        Map<String, PLTDetailDTO> selectedPlt = new HashMap<>();


        Debug.printDebugBoundary();
        dmBaseService.getPlts(username);

        Debug.printDebugBoundary();


        return ResponseEntity.ok(dmBaseService.getPlts(username));

    }

    @PostMapping("/get-companys")
    public ResponseEntity<?> getCompanys(@RequestHeader(value = "Authorization", required = true) String token, @RequestBody PLTDetailDTO selectedPlt){

        System.out.println("In get company "+selectedPlt);
        String uName = null;
        Debug.printDebugBoundary();
        if (token.startsWith("Bearer ")) {
            uName = jwtTokenUtil.extractUsername(token.substring(7)).toLowerCase();
            System.out.println(uName);
        }

        String key = uName + "_" + token.substring(7);
        Debug.printDebugBoundary("\uD83C\uDF39❤\uD83D\uDC96");
        System.out.println(key);
        Debug.printDebugBoundary("\uD83C\uDF39❤\uD83D\uDC96");

        Debug.printDebugBoundary("❤\uD83D\uDC96\uD83C\uDF39\uD83D\uDE4C✌✌");


        String dbCompany = (String) redisObjectUtil.getObjectValue(key, "companyDB");
        String db = redisObjectUtil.getObjectValueAsString(key, "companyDB");

        redisObjectUtil.addFieldToObject(key, "selectedPlt", selectedPlt);

        PLTDetailDTO pltObject = (PLTDetailDTO) redisObjectUtil.getObjectValue(key, "selectedPlt");

        System.out.println("from redis "+pltObject);

        Debug.printDebugBoundary("\uD83C\uDF39❤\uD83D\uDC96");
        System.out.println("username "+uName);
        System.out.println("dbCompany"+ dbCompany);
        Debug.printDebugBoundary("\uD83C\uDF39❤\uD83D\uDC96");



        assert uName != null;

        List<CompanyDetailDTO> companyDetailDTOList = pcsDataMapper.mapToCompanyDetailDTOList(dmBaseService.getCompanys(uName, selectedPlt.getPLTCode(), dbCompany));

        //PCSData pcsData = pcsDataStore.get("pcsContextData");

        PCSData pcsData = new PCSData();


        String userDecription = redisObjectUtil.getObjectValueAsString(key, "userDescription");
        String regionalLanguage = redisObjectUtil.getObjectValueAsString(key, "regionalLanguage");
        String loginDate = redisObjectUtil.getObjectValueAsString(key, "loginDate");


        pcsData.setUserName(uName);
        pcsData.setUserDescription(userDecription);
        pcsData.setRegionalLanguage(regionalLanguage);
        pcsData.setLoginDate(loginDate);
        pcsData.setPltSelected(selectedPlt);


        String pltCode = String.valueOf(selectedPlt.getPLTCode());

        if(companyDetailDTOList != null && companyDetailDTOList.size() == 1){
        //if(true){
            CompanyDetailDTO selectedCompany = companyDetailDTOList.get(0);
            redisObjectUtil.addFieldToObject(key, "selectedCompany", selectedCompany);
            List<SiteDetailsDTO> siteDetailsDTOList = pcsDataMapper.mapToSiteDetailsDTOList(dmBaseService.getSites(uName, externalConfig.getCompanyDb(), String.valueOf(selectedCompany.getComp_Code()), pltCode));
            pcsData.setCSelected(selectedCompany);
        }else{
            pcsData.setLstCompanys(companyDetailDTOList);
        }

        return ResponseEntity.ok(pcsData);

    }

    @PostMapping("/get-sites")
    public ResponseEntity<?> getSites(@RequestHeader(value = "Authorization", required = true) String token, @RequestBody CompanyDetailDTO selectedCompany){

        String uName = null;
        Debug.printDebugBoundary();
        if (token.startsWith("Bearer ")) {
            uName = jwtTokenUtil.extractUsername(token.substring(7)).toLowerCase();
            System.out.println(uName);
        }

        String key = uName + "_" + token.substring(7);
        //redisObjectUtil.addFieldToObject(key, "companyCode", selectedCompanyCode);

        redisObjectUtil.addFieldToObject(key, "selectedCompany", selectedCompany);

        System.out.println("selected Company "+selectedCompany);

        PLTDetailDTO pltDetailDTO = (PLTDetailDTO) redisObjectUtil.getObjectValue(key, "selectedPlt");

        String pltCode = String.valueOf(pltDetailDTO.getPLTCode());

        List<SiteDetailsDTO> siteDetailsDTOList = pcsDataMapper.mapToSiteDetailsDTOList(dmBaseService.getSites(uName, externalConfig.getCompanyDb(), String.valueOf(selectedCompany.getComp_Code()), pltCode));


        String userDecription = redisObjectUtil.getObjectValueAsString(key, "userDescription");
        String regionalLanguage = redisObjectUtil.getObjectValueAsString(key, "regionalLanguage");
        String loginDate = redisObjectUtil.getObjectValueAsString(key, "loginDate");


        PCSData pcsData = new PCSData();
        pcsData.setUserName(uName);
        pcsData.setUserDescription(userDecription);
        pcsData.setRegionalLanguage(regionalLanguage);
        pcsData.setLoginDate(loginDate);
        pcsData.setPltSelected(pltDetailDTO);
        pcsData.setCSelected(selectedCompany);

        if(siteDetailsDTOList != null && siteDetailsDTOList.size() == 1){
            redisObjectUtil.addFieldToObject(key, "selectedSite", siteDetailsDTOList.get(0));
            pcsData.setLstSites(siteDetailsDTOList);
        }else{
            pcsData.setLstSites(siteDetailsDTOList);
        }


        return ResponseEntity.ok(pcsData);
    }
}
