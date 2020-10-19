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
public class AccountTwoController {

    private static final Logger log = LoggerFactory.getLogger(AccountTwoController.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/transfer/increase")
    @Transactional
    public String increaseAmount(String id, double amount) {
        double currentAmount = jdbcTemplate.queryForObject("select amount from t_account_two where id=?", Double.class, id);
        log.info("账户【{}】当前余额【{}】，即将增加【{}】", id, currentAmount, amount);
        int rows = jdbcTemplate.update("update t_account_two set amount = amount + ? where id = ?", amount, id);
        if (rows == 0) {
            throw new IllegalStateException("账户不存在");
        }
        return "success";
    }

}
