package tech.wetech.transacation.context;

import com.ecwid.consul.v1.ConsulClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.wetech.transacation.GlobalTransactionManager;
import tech.wetech.transacation.integration.consul.ConsulLockStore;
import tech.wetech.transacation.integration.consul.ConsulStatusStore;
import tech.wetech.transacation.store.LockStore;
import tech.wetech.transacation.store.StatusStore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author cjbi
 */
public class TransactionContext {

    private final Logger log = LoggerFactory.getLogger(GlobalTransactionManager.class);
    /**
     * The constant KEY_XID.
     */
    public static final String KEY_XID = "TX_XID";
    /**
     * The constant KEY_GLOBAL_LOCK_FLAG, VALUE_GLOBAL_LOCK_FLAG
     */
    public static final String KEY_GLOBAL_LOCK_FLAG = "TX_LOCK";
    public static final Boolean VALUE_GLOBAL_LOCK_FLAG = true;

    private ThreadLocal<Map<String, Object>> resources = ThreadLocal.withInitial(HashMap::new);

    private final LockStore lockStore;

    private final StatusStore statusStore;

    private String nodeKey;

    public TransactionContext(ConsulClient consulClient) {
        this.lockStore = new ConsulLockStore(consulClient);
        this.statusStore = new ConsulStatusStore(consulClient);
        initTransactionContextHolder();
    }

    private void initTransactionContextHolder() {
        TransactionContextHolder.setTransactionContext(this);
    }

    public Object put(String key, Object value) {
        return resources.get().put(key, value);
    }

    public Object get(String key) {
        return resources.get().get(key);
    }

    public Object remove(String key) {
        return resources.get().remove(key);
    }

    public void putAll(Map<String, Object> entries) {
        resources.get().putAll(entries);
    }

    public Map<String, Object> entries() {
        return resources.get();
    }

    public LockStore getLockStore() {
        return lockStore;
    }

    public StatusStore getStatusStore() {
        return statusStore;
    }

    public String getNodeKey() {
        if (nodeKey == null) {
            return UUID.randomUUID().toString();
        }
        return nodeKey;
    }

    /**
     * Gets xid.
     *
     * @return the xid
     */
    public String getXID() {
        return (String) get(KEY_XID);
    }

    /**
     * Bind.
     *
     * @param xid the xid
     */
    public void bind(String xid) {
        if (log.isDebugEnabled()) {
            log.debug("bind {}", xid);
        }
        put(KEY_XID, xid);
    }

    /**
     * Unbind string.
     *
     * @return the string
     */
    public String unbind() {
        String xid = (String) remove(KEY_XID);
        if (log.isDebugEnabled()) {
            log.debug("unbind {} ", xid);
        }
        return xid;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    /**
     * declare local transactions will use global lock check for update/delete/insert/selectForUpdate SQL
     */
    public void bindGlobalLockFlag() {
        if (log.isDebugEnabled()) {
            log.debug("Local Transaction Global Lock support enabled");
        }
        //just put something not null
        put(KEY_GLOBAL_LOCK_FLAG, VALUE_GLOBAL_LOCK_FLAG);
    }

    public void unbindGlobalLockFlag() {
        Boolean lockFlag = (Boolean) remove(KEY_GLOBAL_LOCK_FLAG);
        if (log.isDebugEnabled() && lockFlag != null) {
            log.debug("unbind global lock flag");
        }
    }

    public boolean holdLock() {
        return get(TransactionContext.KEY_GLOBAL_LOCK_FLAG) != null;
    }

    public void clear() {
        resources.remove();
    }

}
