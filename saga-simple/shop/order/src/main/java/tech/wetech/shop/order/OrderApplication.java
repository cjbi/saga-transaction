package tech.wetech.shop.order;

import com.ecwid.consul.v1.ConsulClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import tech.wetech.transacation.GlobalTransactionManager;
import tech.wetech.transacation.context.TransactionContext;

import javax.persistence.EntityManagerFactory;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = "tech.wetech")
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    /**
     * 配置事务管理器
     * @param consulClient
     * @param serviceInstance
     * @param emf
     * @return
     */
    @Bean
    public GlobalTransactionManager transactionManager(ConsulClient consulClient, ServiceInstance serviceInstance, EntityManagerFactory emf) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager(emf);
        TransactionContext transactionContext = new TransactionContext(consulClient);
        transactionContext.setNodeKey(serviceInstance.getInstanceId());
        return new GlobalTransactionManager(transactionContext, jpaTransactionManager);
    }


}
