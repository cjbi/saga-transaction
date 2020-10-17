package tech.wetech.transacation.context;

/**
 * @author cjbi
 */
public class TransactionContextHolder {

    private static TransactionContext INSTANCE = null;

    private TransactionContextHolder() {
    }

    public static void setTransactionContext(final TransactionContext transactionContext) {
        TransactionContextHolder.INSTANCE = transactionContext;
    }

    public static TransactionContext getTransactionContext() {
        return INSTANCE;
    }

}
