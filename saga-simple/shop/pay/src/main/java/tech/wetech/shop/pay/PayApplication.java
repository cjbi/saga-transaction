package tech.wetech.shop.pay;

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
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import tech.wetech.transacation.GlobalTransactionManager;
import tech.wetech.transacation.integration.consul.ConsulLockStore;
import tech.wetech.transacation.integration.consul.ConsulStatusStore;

import javax.sql.DataSource;

/**
 * @author cjbi
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = "tech.wetech")
public class PayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
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
    public GlobalTransactionManager transactionManager(ConsulClient consulClient, ServiceInstance serviceInstance, DataSource dataSource) {
        DataSourceTransactionManager dtm = new DataSourceTransactionManager(dataSource);
        GlobalTransactionManager gtm = new GlobalTransactionManager(dtm);
        //设置锁存储
        gtm.setLockStore(new ConsulLockStore(consulClient));
        //设置状态存储
        gtm.setStatusStore(new ConsulStatusStore(consulClient));
        //设置节点名称
        gtm.setNodeKey(serviceInstance.getInstanceId());
        return gtm;
    }

    @Autowired
    private ApplicationContext applicationContext;

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
