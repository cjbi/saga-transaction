package tech.wetech.account;

import com.ecwid.consul.v1.ConsulClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import tech.wetech.transacation.GlobalTransactionManager;
import tech.wetech.transacation.integration.consul.ConsulLockStore;
import tech.wetech.transacation.integration.consul.ConsulStatusStore;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = "tech.wetech")
public class AccountTwoBackupApplication {

    @Autowired
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(AccountTwoBackupApplication.class, args);
    }

    /**
     * 配置事务管理器
     *
     * @param consulClient
     * @param serviceInstance
     * @param dataSource
     * @return
     */
    @Bean
    public GlobalTransactionManager transactionManager(ConsulClient consulClient, ServiceInstance serviceInstance, DataSource dataSource, MongoDatabaseFactory mongoDatabaseFactory) {
        //JDBC的事务管理器
        DataSourceTransactionManager dtm = new DataSourceTransactionManager(dataSource);
        //mongodb的事务管理器
        MongoTransactionManager mtm = new MongoTransactionManager(mongoDatabaseFactory);
        //全局事务管理器
        GlobalTransactionManager gtm = new GlobalTransactionManager(dtm, mtm);
        //设置锁存储
        gtm.setLockStore(new ConsulLockStore(consulClient));
        //设置状态存储
        gtm.setStatusStore(new ConsulStatusStore(consulClient));
        //设置节点名称
        gtm.setNodeKey(serviceInstance.getInstanceId());
        return gtm;
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
            //初始化mongo数据
            MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
            mongoTemplate.remove(Query.query(Criteria.where("id").is(1)), "account_two_backup_1");
            mongoTemplate.remove(Query.query(Criteria.where("id").is(1)), "account_two_backup_2");
            mongoTemplate.remove(Query.query(Criteria.where("id").is(1)), "account_two_backup_3");
            Map<String, Object> mongoData = new HashMap<>();
            mongoData.put("id", 1L);
            mongoData.put("amount", 100);
            mongoTemplate.insert(mongoData, "account_two_backup_1");
            mongoTemplate.insert(mongoData, "account_two_backup_2");
            mongoTemplate.insert(mongoData, "account_two_backup_3");
        };
    }

}
