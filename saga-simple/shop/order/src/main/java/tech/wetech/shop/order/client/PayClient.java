package tech.wetech.shop.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 支付服务
 *
 * @author cjbi
 */
@FeignClient("saga-simple-pay")
public interface PayClient {

    @PutMapping("pay/payment")
    public String payment(@RequestParam("userId") String userId,@RequestParam("price") BigDecimal price);

}
