package dataman.erp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Component
@PropertySource("file:C:/Users/Dataman/myconfigfile/myconfig.properties")
public class ExternalConfig {

    @Value("${sqlHostName}")
    private String sqlHostName;

    @Value("${sqlPort}")
    private String sqlPort;

    @Value("${sqlUser}")
    private String sqlUser;

    @Value("${sqlPassword}")
    private String sqlPassword;

    @Value("${companyDb}")
    private String companyDb;

    @Value("${serverPort}")
    private String serverPort;

    @Value("${serverAddress}")
    private String serverAddress;

    @Value("${mongoHost}")
    private String mongoHost;

    @Value("${mongoPort}")
    private String mongoPort;

    @Value("${mongoUser}")
    private String mongoUser;

    @Value("${mongoPassword}")
    private String mongoPassword;
}
