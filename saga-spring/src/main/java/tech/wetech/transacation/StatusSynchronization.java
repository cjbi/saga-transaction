package tech.wetech.transacation;

import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import tech.wetech.transacation.context.TransactionContext;
import tech.wetech.transacation.context.TransactionContextHolder;

/**
 * Cleanup action after transaction is completed.
 *
 * @author cjbi
 */
public class StatusSynchronization extends TransactionSynchronizationAdapter {

    public static final StatusSynchronization INSTANCE = new StatusSynchronization();

    @Override
    public void afterCompletion(int status) {
        TransactionContext transactionContext = TransactionContextHolder.getTransactionContext();
        String xid = transactionContext.getXID();
        //After, release the lock, and cleanup the threadLocal resources.
        if (TransactionContextHolder.getTransactionContext().holdLock()) {
            TransactionContextHolder.getTransactionContext().getLockStore().releaseLock(xid);
        }
        TransactionContextHolder.getTransactionContext().clear();
    }

}
