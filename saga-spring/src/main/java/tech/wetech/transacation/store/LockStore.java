package tech.wetech.transacation.store;

/**
 * @author cjbi
 */
public interface LockStore {

    /**
     * Acquire lock boolean.
     *
     * @param xid transaction id.
     * @return
     */
    boolean acquireLock(String xid);

    /**
     * Un lock boolean.
     *
     * @param xid
     * @return
     */
    boolean releaseLock(String xid);

}
