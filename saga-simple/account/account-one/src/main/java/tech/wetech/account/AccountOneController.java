package tech.wetech.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟多数据源场景转账的业务，有一方失败则回滚事务
 *
 * @author cjbi
 */
@RestController
public class AccountOneController {

    private static final Logger log = LoggerFactory.getLogger(AccountOneController.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AccountTwoClient accountTwoClient;

    @PostMapping("/transfer/decrease")
    @Transactional
    public String decreaseAmount(String id, double amount) {
        double currentAmount = jdbcTemplate.queryForObject("select amount from t_account_one where id=?", Double.class, id);
        log.info("账户【{}】当前余额【{}】，即将减少【{}】", id, currentAmount, amount);
        accountTwoClient.increaseAmount(id, amount);
        int rows = jdbcTemplate.update("update t_account_one set amount = amount - ? where amount>=? and id = ?", amount, amount, id);
        if (rows == 0) {
            throw new IllegalStateException("余额不足");
        }
        return "success";
    }

}
