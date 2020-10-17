package tech.wetech.transacation.integration.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.kv.model.PutParams;
import com.ecwid.consul.v1.session.model.NewSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.wetech.transacation.context.TransactionContextHolder;
import tech.wetech.transacation.store.LockStore;

import java.time.LocalDateTime;

/**
 * @author cjbi
 */
public class ConsulLockStore implements LockStore {

    public static final Logger log = LoggerFactory.getLogger(ConsulStatusStore.class);
    public static final String CONSUL_SESSION_KEY = "CONSUL_SESSION_ID";
    private final ConsulClient consulClient;

    public ConsulLockStore(ConsulClient consulClient) {
        this.consulClient = consulClient;
    }

    @Override
    public boolean acquireLock(String xid) {
        if (xid == null) {
            return false;
        }
        String sessionId = createSession(xid);
        TransactionContextHolder.getTransactionContext().put(CONSUL_SESSION_KEY, sessionId);
        log.debug("Acquiring Global Lock!, xid={} ", xid);
        while (true) {
            PutParams putParams = new PutParams();
            putParams.setAcquireSession(sessionId);
            if (consulClient.setKVValue("transaction/" + xid + "/lock", "lock:" + LocalDateTime.now(), putParams).getValue()) {
                log.debug("Acquired Transaction Global Lock, xid={}", xid);
                return true;
            } else {
                continue;
            }
        }
    }

    /**
     * 释放同步锁
     *
     * @param xid
     * @return
     */
    @Override
    public boolean releaseLock(String xid) {
        log.debug("Release Transaction Global Lock, xid={}", xid);
        String sessionId = (String) TransactionContextHolder.getTransactionContext().get(CONSUL_SESSION_KEY);
        if (sessionId == null) {
            return false;
        }
        TransactionContextHolder.getTransactionContext().remove(CONSUL_SESSION_KEY);
        PutParams putParams = new PutParams();
        putParams.setReleaseSession(sessionId);
        boolean result = consulClient.setKVValue("transaction/" + xid + "/lock", "unlock:" + LocalDateTime.now(), putParams).getValue();
        consulClient.sessionDestroy(sessionId, null);
        return result;
    }

    /**
     * 创建session
     *
     * @param sessionName
     * @return
     */
    private String createSession(String sessionName) {
        NewSession newSession = new NewSession();
        newSession.setName(sessionName);
        return consulClient.sessionCreate(newSession, null).getValue();
    }
}
