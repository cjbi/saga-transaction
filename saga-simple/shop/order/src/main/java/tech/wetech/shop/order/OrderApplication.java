package tech.wetech.shop.order;

import com.ecwid.consul.v1.ConsulClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import tech.wetech.transacation.GlobalTransactionManager;
import tech.wetech.transacation.integration.consul.ConsulLockStore;
import tech.wetech.transacation.integration.consul.ConsulStatusStore;

import javax.persistence.EntityManagerFactory;
import java.util.Arrays;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = "tech.wetech")
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    /**
     * 配置事务管理器
     *
     * @param consulClient
     * @param serviceInstance
     * @param emf
     * @return
     */
    @Bean
    public GlobalTransactionManager transactionManager(ConsulClient consulClient, ServiceInstance serviceInstance, EntityManagerFactory emf) {
        JpaTransactionManager jtm = new JpaTransactionManager(emf);
        GlobalTransactionManager gtm = new GlobalTransactionManager(jtm);
        //设置锁存储
        gtm.setLockStore(new ConsulLockStore(consulClient));
        //设置状态存储
        gtm.setStatusStore(new ConsulStatusStore(consulClient));
        //设置节点名称
        gtm.setNodeKey(serviceInstance.getInstanceId());
        //设置忽略的资源清单
        gtm.setIgnoreCleanupResources(Arrays.asList(LocalContainerEntityManagerFactoryBean.class));
        return gtm;
    }


}
