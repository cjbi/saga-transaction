package tech.wetech.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = "tech.wetech")
public class AccountOneApplication {

    @Autowired
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(AccountOneApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            //初始化数据
            DataSource dataSource = applicationContext.getBean(DataSource.class);
            ResourceLoader loader = new DefaultResourceLoader();
            Resource schema = loader.getResource("classpath:schema.sql");
            Resource data = loader.getResource("classpath:data.sql");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator(schema, data);
            populator.execute(dataSource);
        };
    }

}
