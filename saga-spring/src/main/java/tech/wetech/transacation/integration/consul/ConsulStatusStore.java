package tech.wetech.transacation.integration.consul;

import com.ecwid.consul.v1.ConsistencyMode;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetBinaryValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.wetech.transacation.store.StatusStore;

import java.util.*;

/**
 * @author cjbi
 */
public class ConsulStatusStore implements StatusStore {

    private ConsulClient consulClient;

    private static final Logger log = LoggerFactory.getLogger(ConsulStatusStore.class);

    public ConsulStatusStore(ConsulClient consulClient) {
        this.consulClient = consulClient;
    }

    @Override
    public void saveTransactionStatus(String xid, String nodeKey, boolean status) {
        log.debug("Begin Save CurrentNode TransactionStatus to Consul, xid={}, nodeKey={}, status={}", xid, nodeKey, status);
        consulClient.setKVValue("transaction/" + xid + "/branch/" + nodeKey, Objects.toString(status));
    }

    @Override
    public List<Map<String, Boolean>> listTransactionStatus(String xid) {
        List<Map<String, Boolean>> statusList = new ArrayList<>();
        QueryParams queryParams = new QueryParams(ConsistencyMode.CONSISTENT);
        Response<List<GetBinaryValue>> kvValues = consulClient.getKVBinaryValues("transaction/" + xid + "/branch", queryParams);
        if (kvValues.getValue() == null) {
            return Collections.emptyList();
        }
        for (GetBinaryValue getBinaryValue : kvValues.getValue()) {
            Map<String, Boolean> statusMap = new HashMap<>();
            statusMap.put(getBinaryValue.getKey(), Boolean.valueOf(new String(getBinaryValue.getValue())));
            statusList.add(statusMap);
        }
        return statusList;
    }
}
