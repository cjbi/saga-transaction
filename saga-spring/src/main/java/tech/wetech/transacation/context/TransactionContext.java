package tech.wetech.transacation.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.wetech.transacation.GlobalTransactionManager;
import tech.wetech.transacation.store.LockStore;
import tech.wetech.transacation.store.StatusStore;

import java.util.*;

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

    private ThreadLocal<Map<String, Object>> entries = ThreadLocal.withInitial(HashMap::new);

    private LockStore lockStore;

    private StatusStore statusStore;

    private String nodeKey;

    /**
     * configure ignore cleanup synchronization resources.
     */
    private List<Class<?>> ignoreCleanupResources = new ArrayList<>();

    public Object put(String key, Object value) {
        return entries.get().put(key, value);
    }

    public Object get(String key) {
        return entries.get().get(key);
    }

    public Object remove(String key) {
        return entries.get().remove(key);
    }

    public void putAll(Map<String, Object> entries) {
        this.entries.get().putAll(entries);
    }

    public Map<String, Object> entries() {
        return entries.get();
    }

    public LockStore getLockStore() {
        if (lockStore == null) {
            throw new IllegalStateException("LockStore or StatusStore can not be null, Please configure the implementation.");
        }
        return lockStore;
    }

    public StatusStore getStatusStore() {
        if (statusStore == null) {
            throw new IllegalStateException("LockStore or StatusStore can not be null, Please configure the implementation.");
        }
        return statusStore;
    }

    public String getNodeKey() {
        if (nodeKey == null) {
            this.nodeKey = UUID.randomUUID().toString();
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
            log.debug("Bind {}", xid);
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

    public void setLockStore(LockStore lockStore) {
        this.lockStore = lockStore;
    }

    public void setStatusStore(StatusStore statusStore) {
        this.statusStore = statusStore;
    }

    public List<Class<?>> getIgnoreCleanupResources() {
        return ignoreCleanupResources;
    }

    public void setIgnoreCleanupResources(List<Class<?>> ignoreCleanupResources) {
        this.ignoreCleanupResources = ignoreCleanupResources;
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
        entries.remove();
    }

}
