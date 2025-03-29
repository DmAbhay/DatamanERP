package dataman.erp.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "dlms.loan.userMast.repository",
        entityManagerFactoryRef = "companyTirangaEntityManagerFactory",
        transactionManagerRef = "companyTirangaTransactionManager"
)
public class TirangaDataSourceConfig {

    //companyDlmTransactionManager  companyTirangaDataSource  companyDlmDataSource

    @Autowired
    private ExternalConfig externalConfig;  // Ensure this class is defined and injected correctly here we companyDb name becouse company tabel is here

    @Bean(name = "companyTirangaDataSource")
    DataSource companyTirangaDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:sqlserver://" + externalConfig.getSqlHostName() + ":" + externalConfig.getSqlPort() +
                        ";databaseName=" + "tiranga_Company" + ";encrypt=true;trustServerCertificate=true")
                .username(externalConfig.getSqlUser())
                .password(externalConfig.getSqlPassword())
                .driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
                .build();

    }

    @Bean(name = "companyTirangaEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean companyTirangaEntityManagerFactory(
            @Qualifier("companyTirangaDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("dlms.loan.singup.entity"); // Adjust to where your entity classes are located
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter()); // Set Hibernate as JPA provider
        factory.setJpaProperties(jpaProperties()); // Set JPA properties
        return factory;
    }

    @Bean(name = "companyTirangaTransactionManager")
    JpaTransactionManager companyTirangaTransactionManager(
            @Qualifier("companyTirangaEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

    // Method to define JPA properties
    private Properties jpaProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        return properties;
    }

    @Bean(name = "tirangaCompanyJdbcTemplate")
    JdbcTemplate tirangaCompanyJdbcTemplate(@Qualifier("companyTirangaDataSource") DataSource dlmTransactionDataSource) {
        return new JdbcTemplate(dlmTransactionDataSource);
    }

    @Bean(name = "tirangaCompanyNamedJdbcTemplate")
    public NamedParameterJdbcTemplate tirangaCompanyNamedJdbcTemplate(
            @Qualifier("tirangaCompanyJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new NamedParameterJdbcTemplate(jdbcTemplate);
    }


}
