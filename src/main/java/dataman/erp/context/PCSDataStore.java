package dataman.erp.context;

import dataman.erp.dmbase.models.PCSData;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PCSDataStore {
    private final ConcurrentHashMap<String, PCSData> store = new ConcurrentHashMap<>();

    public void save(String userName, PCSData pcsData) {
        store.put(userName, pcsData);
    }

    public PCSData get(String userName) {
        return store.get(userName);
    }

    public void remove(String userName) {
        store.remove(userName);
    }
}
