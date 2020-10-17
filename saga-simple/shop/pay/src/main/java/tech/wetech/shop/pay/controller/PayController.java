package tech.wetech.shop.pay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.wetech.shop.pay.service.PayService;

import java.math.BigDecimal;

/**
 * @author cjbi
 */
@RestController
public class PayController {

    @Autowired
    private PayService payService;

    @GetMapping("/hello")
    public String hello() {
        return "hello world!";
    }

    @PutMapping("pay/payment")
    public String payment(String userId, BigDecimal price) {
        payService.payment(userId, price);
        return "success";
    }

}
