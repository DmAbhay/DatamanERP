package dataman.erp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import dataman.erp.auth.service.UserMastService;
import dataman.erp.dto.TestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private UserMastService userMastService;

    @PostMapping("/rest")
    public ResponseEntity<?> doTest(@RequestBody JsonNode payload){


        //System.out.println(userMastService.loadUserByUsername("anup"));
        System.out.println(userMastService.isUserExist("anup"));

        TestDTO testDTO = new TestDTO();
        testDTO.setUserName(payload.get("username").asText());
        testDTO.setPassword(payload.get("password").asText());
        testDTO.setAge(payload.get("age").asInt());
        testDTO.setGender(payload.get("gender").asText());
        return ResponseEntity.ok(testDTO);
    }
}
