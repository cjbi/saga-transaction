package tech.wetech.shop.order.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.shop.order.client.PayClient;
import tech.wetech.shop.order.client.StockClient;
import tech.wetech.shop.order.entity.Order;
import tech.wetech.shop.order.repository.OrderRepository;
import tech.wetech.shop.order.service.OrderService;

import java.math.BigDecimal;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private StockClient stockClient;
    @Autowired
    private PayClient payClient;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public void submit(Order order) {
        //保存订单
        log.info("正在保存订单, goodsId={}, number={}", order.getGoodsId(), order.getNumber());
        orderRepository.save(order);
        //库存扣减
        log.info("正在库存扣减, goodsId={}, number={}", order.getGoodsId(), order.getNumber());
        stockClient.reduce(order.getGoodsId(), order.getNumber());
        //支付订单
        BigDecimal orderPrice = order.getGoodsPrice().multiply(BigDecimal.valueOf(order.getNumber()));
        log.info("正在支付订单, goodsId={}, number={}, orderPrice={}", order.getGoodsId(), order.getNumber(), orderPrice);
        payClient.payment(order.getUserId(), orderPrice);
    }


}
