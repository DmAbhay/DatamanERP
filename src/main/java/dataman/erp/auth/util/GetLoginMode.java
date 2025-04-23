package dataman.erp.auth.util;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

public class GetLoginMode {

    public static String getLoginMode(String input){
        String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        //String MOBILE_REGEX = "^[6-9]{10}$";
        String MOBILE_REGEX = "^[6-9]\\d{9}$";

        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        if (Pattern.matches(EMAIL_REGEX, input)) {
            return "EMAIL";
        } else if (Pattern.matches(MOBILE_REGEX, input)) {
            return "MOBILE";
        } else {
            return "USERNAME";
        }
    }
}
