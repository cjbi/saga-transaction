package tech.wetech.shop.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 库存服务
 * @author cjbi
 */
@FeignClient("saga-simple-stock")
public interface StockClient {

    @PutMapping("/stock/reduce")
    String reduce(@RequestParam("goodsId") Long goodsId, @RequestParam("quantity") Long quantity);

}
