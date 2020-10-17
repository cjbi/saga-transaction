package tech.wetech.transacation.integration.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import tech.wetech.transacation.context.TransactionContext;
import tech.wetech.transacation.context.TransactionContextHolder;

import java.util.Collection;
import java.util.Map;

/**
 * @author cjbi
 */
public class TransactionFeignInterceptor implements RequestInterceptor {

    public static final String HEADER_TRANSACTION_XID_KEY = "X-TRANSACTION-XID";

    @Override
    public void apply(RequestTemplate template) {
        Map<String, Collection<String>> headers = template.headers();
        String xid = (String) TransactionContextHolder.getTransactionContext().get(TransactionContext.KEY_XID);
        if (!headers.containsKey(HEADER_TRANSACTION_XID_KEY)) {
            template.header(HEADER_TRANSACTION_XID_KEY, xid);
        }
    }

}
