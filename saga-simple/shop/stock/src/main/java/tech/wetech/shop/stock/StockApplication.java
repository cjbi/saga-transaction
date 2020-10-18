package tech.wetech.shop.stock;

import com.ecwid.consul.v1.ConsulClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import tech.wetech.shop.stock.entity.Stock;
import tech.wetech.shop.stock.repository.StockRepository;
import tech.wetech.transacation.GlobalTransactionManager;
import tech.wetech.transacation.integration.consul.ConsulLockStore;
import tech.wetech.transacation.integration.consul.ConsulStatusStore;

import javax.persistence.EntityManagerFactory;
import java.util.Arrays;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "tech.wetech")
public class StockApplication {
    @Autowired
    private StockRepository stockRepository;

    public static void main(String[] args) {
        SpringApplication.run(StockApplication.class, args);
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
        return gtm;
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            //初始化数据
            Stock stock1 = new Stock();
            stock1.setGoodsId(1L);
            stock1.setAmount(100L);
            Stock stock2 = new Stock();
            stock2.setGoodsId(2L);
            stock2.setAmount(20L);
            Stock stock3 = new Stock();
            stock3.setGoodsId(3L);
            stock3.setAmount(30L);
            stockRepository.saveAll(Arrays.asList(stock1, stock2, stock3));
        };
    }

}
