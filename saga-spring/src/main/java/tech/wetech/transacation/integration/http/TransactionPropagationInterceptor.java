package tech.wetech.transacation.integration.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import tech.wetech.transacation.context.TransactionContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author cjbi
 */
public class TransactionPropagationInterceptor extends HandlerInterceptorAdapter {

    public static final String HEADER_TRANSACTION_XID_KEY = "X-TRANSACTION-XID";
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionPropagationInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String rpcXid = request.getHeader(HEADER_TRANSACTION_XID_KEY);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("xid in TransactionPropagationInterceptor[{}]", rpcXid);
        }
        if (rpcXid != null) {
            TransactionContextHolder.getTransactionContext().bind(rpcXid);
        }
        return true;
    }

}
