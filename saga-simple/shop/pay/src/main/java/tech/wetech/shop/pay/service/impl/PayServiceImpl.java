package tech.wetech.shop.pay.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.shop.pay.exception.PayException;
import tech.wetech.shop.pay.mapper.UserWalletMapper;
import tech.wetech.shop.pay.service.PayService;

import java.math.BigDecimal;

@Service
public class PayServiceImpl implements PayService {

    private static final Logger log = LoggerFactory.getLogger(PayServiceImpl.class);

    @Autowired
    private UserWalletMapper userWalletMapper;

    @Override
    @Transactional
    public void payment(String userId, BigDecimal price) {
        BigDecimal money = userWalletMapper.selectMoneyByUserId(userId);
        log.info("用户【{}】账户余额【{}】，即将支付费用【{}】，", userId, money, price);
        int rows = userWalletMapper.payment(userId, price);
        if (rows == 0) {
            throw new PayException("余额不足");
        }
    }
}
