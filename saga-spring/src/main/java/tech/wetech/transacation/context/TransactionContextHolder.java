package tech.wetech.transacation.context;
/**
 * @author cjbi
 */
public class TransactionContextHolder {

    private static TransactionContext instance = null;

    private TransactionContextHolder() {
    }

    public static void initTransactionContext() {
        instance = new TransactionContext();
    }

    public static TransactionContext getTransactionContext() {
        return instance;
    }

}
