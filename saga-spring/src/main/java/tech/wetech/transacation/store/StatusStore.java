package tech.wetech.transacation.store;

import java.util.List;
import java.util.Map;

/**
 * @author cjbi
 */
public interface StatusStore {

    /**
     * save the global transaction status to store.
     *
     * @param xid    transaction id
     * @param status success or failure
     */
    void saveTransactionStatus(String xid, String nodeKey, boolean status);

    /**
     * list the global transaction status in the store.
     *
     * @param xid transaction id
     * @return transaction status list
     */
    List<Map<String, Boolean>> listTransactionStatus(String xid);

}
