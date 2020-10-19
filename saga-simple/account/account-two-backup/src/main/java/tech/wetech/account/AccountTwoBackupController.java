package tech.wetech.account;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟跨数据库+多数据源场景转账的业务，有一方失败则回滚事务
 *
 * @author cjbi
 */
@RestController
public class AccountTwoBackupController {

    private static final Logger log = LoggerFactory.getLogger(AccountTwoBackupController.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostMapping("/transfer/increase")
    @Transactional
    public String increaseAmount(String id, double amount) {
        double currentAmount = jdbcTemplate.queryForObject("select amount from t_account_two_backup where id=?", Double.class, id);
        log.info("账户【{}】当前余额【{}】，即将增加【{}】", id, currentAmount, amount);
        jdbcTemplate.update("update t_account_two_backup set amount = amount + ? where id = ?", amount, id);
        //同步更新mongo的数据(请注意：mongodb不支持单机事务，4.0支持跨文档事务（复制集）,4.2支持分片事务（cluster集群）)
        mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(1)), Update.fromDocument(Document.parse("{$inc:{\"amount\":" + amount + "}}")), "account_two_backup_1");
        mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(1)), Update.fromDocument(Document.parse("{$inc:{\"amount\":" + amount + "}}")), "account_two_backup_2");
        mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(1)), Update.fromDocument(Document.parse("{$inc:{\"amount\":" + amount + "}}")), "account_two_backup_3");
        return "success";
    }

}
