package tech.wetech.shop.order.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.wetech.shop.order.entity.Order;
import tech.wetech.shop.order.service.OrderService;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/hello")
    public String hello() {
        return "hello world!";
    }

    @PostMapping("/order/submit")
    public String submit(Order order) {
        orderService.submit(order);
        return "success";
    }

}
