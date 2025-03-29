package dataman.erp.dmbase.service;

import dataman.erp.dmbase.repository.DmBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DmBaseService {

    @Autowired
    private DmBaseRepository dmBaseRepository;

    public List<Map<String, Object>> getPlts(String username){
        if(username.trim().equalsIgnoreCase("SA") || username.trim().equalsIgnoreCase("SUPER")){
            return dmBaseRepository.getPLTDetailForSuparAndSA();
        }else{
            return dmBaseRepository.getPLTDetailsForUser(username);
        }
    }

    public List<Map<String, Object>> getCompanys(String username, int pltCode, String dbCompany){
        System.out.println(username+"Caaaaaaaaaaaaaallllllllllllllllllllllllleeeeeeeeeeeeeeeeeeddddddddddddddd");
        if(username.trim().equalsIgnoreCase("SA") || username.trim().equalsIgnoreCase("SUPER")){
            System.out.println("Callllllllllllllllled");
            return dmBaseRepository.getCompanysForSuperAndSA(pltCode, dbCompany);
        }else{
            System.out.println();
            return dmBaseRepository.getCompanysForUser(pltCode, dbCompany, username);
        }
    }

    public List<Map<String, Object>> getSites(String userName, String dbCompany, String compCode, String pltCode){
        return dmBaseRepository.getSites(dbCompany, userName, compCode, pltCode);
    }
}
