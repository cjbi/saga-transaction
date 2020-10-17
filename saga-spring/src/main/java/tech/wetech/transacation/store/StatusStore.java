package tech.wetech.transacation.store;

import java.util.List;
import java.util.Map;

/**
 * @author cjbi
 */
public interface StatusStore {

    /**
     * 设置事务状态
     *
     * @param xid
     * @param status
     */
    void saveTransactionStatus(String xid, String nodeKey, boolean status);

    /**
     * 获取事务状态
     *
     * @param xid
     * @return
     */
    List<Map<String, Boolean>> listTransactionStatus(String xid);

}
