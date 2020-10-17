package tech.wetech.transacation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ReflectionUtils;
import tech.wetech.transacation.context.TransactionContext;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 全局的事务管理器，通过consul协调分布式场景的Saga事务模型。
 *
 * @author cjbi
 */
public class GlobalTransactionManager implements PlatformTransactionManager {

    private final Logger log = LoggerFactory.getLogger(GlobalTransactionManager.class);
    private final TransactionContext transactionContext;
    private final ChainedTransactionManager delegate;

    public GlobalTransactionManager(TransactionContext transactionContext, PlatformTransactionManager... transactionManagers) {
        this.transactionContext = transactionContext;
        this.delegate = new ChainedTransactionManager(transactionManagers);
    }

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        String xid = transactionContext.getXID();
        if (xid == null) {
            //Generated xid
            xid = UUID.randomUUID().toString();
            transactionContext.bind(xid);
            //Add global lock
            transactionContext.getLockStore().acquireLock(xid);
            transactionContext.bindGlobalLockFlag();
        }
        TransactionStatus transaction = delegate.getTransaction(definition);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(StatusSynchronization.INSTANCE);
        }
        return transaction;
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {
        String xid = transactionContext.getXID();
        transactionContext.getStatusStore().saveTransactionStatus(xid, transactionContext.getNodeKey(), true);
        if (transactionContext.holdLock() || xid == null) {
            doCommit(status, xid);
        } else {
            //If it is not the transaction initiator, Async commit transaction
            Map<Object, Object> resources = TransactionSynchronizationManager.getResourceMap();
            List<TransactionSynchronization> synchronizations = TransactionSynchronizationManager.getSynchronizations();
            String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();
            boolean currentTransactionReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            Integer currentTransactionIsolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
            boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            Map<String, Object> entries = transactionContext.entries();
            CompletableFuture.supplyAsync(() -> {
                try {
                    TransactionSynchronizationManager.initSynchronization();
                    resources.forEach((key, value) -> {
                        if (TransactionSynchronizationManager.getResource(key) == null) {
                            TransactionSynchronizationManager.bindResource(key, value);
                        }
                    });
                    synchronizations.forEach(TransactionSynchronizationManager::registerSynchronization);
                    TransactionSynchronizationManager.setCurrentTransactionName(currentTransactionName);
                    TransactionSynchronizationManager.setCurrentTransactionReadOnly(currentTransactionReadOnly);
                    TransactionSynchronizationManager.setCurrentTransactionIsolationLevel(currentTransactionIsolationLevel);
                    TransactionSynchronizationManager.setActualTransactionActive(actualTransactionActive);
                    transactionContext.putAll(entries);
                    doCommit(status, xid);
                } catch (Exception e) {
                    log.error("");
                    rollback(status);
                }
                return null;
            });
            //Sync cleanup resources
            doCleanupTransferTransactionResources();
        }
    }

    public void doCleanupTransferTransactionResources() {
        try {
            Field field = ReflectionUtils.findField(TransactionSynchronizationManager.class, "resources", ThreadLocal.class);
            field.setAccessible(true);
            NamedThreadLocal resourcesT = (NamedThreadLocal) field.get(null);
            resourcesT.remove();
        } catch (IllegalAccessException e) {
        }
        TransactionSynchronizationManager.clear();
    }


    /**
     * Begin Committing Transaction.
     *
     * @param status
     * @param xid
     */
    private boolean doCommit(TransactionStatus status, String xid) throws TransactionException {
        //写入服务状态为成功
        if (!transactionContext.holdLock()) {
            //阻塞获取同步锁
            transactionContext.getLockStore().acquireLock(xid);
            //获得锁的标识
            transactionContext.bindGlobalLockFlag();
        }
        //获取所有分子事务的状态全部成功则提交事务，有一个失败则回滚事务
        List<Map<String, Boolean>> list = transactionContext.getStatusStore().listTransactionStatus(xid);
        boolean successFlag = true;
        String exceptionNode = null;
        for (Map<String, Boolean> map : list) {
            for (String key : map.keySet()) {
                if (!map.get(key)) {
                    successFlag = false;
                    exceptionNode = key;
                }
            }
        }
        if (!successFlag) {
            log.warn("Rollback Global Transaction, originated at (" + exceptionNode + ") ");
            rollback(status);
        } else {
            log.debug("Commit Transaction, XID={}", xid);
            delegate.commit(status);
        }
        return true;
    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {
        String xid = transactionContext.getXID();
        log.debug("Rollback Transaction, XID={}", xid);
        transactionContext.getStatusStore().saveTransactionStatus(xid, transactionContext.getNodeKey(), false);
        delegate.rollback(status);
    }
}
