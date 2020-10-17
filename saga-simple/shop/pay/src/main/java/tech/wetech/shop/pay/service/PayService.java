package tech.wetech.shop.pay.service;

import java.math.BigDecimal;

/**
 * @author cjbi
 */
public interface PayService {

    /**
     * 付款
     * @param userId
     * @param price
     */
    void payment(String userId, BigDecimal price);

}
