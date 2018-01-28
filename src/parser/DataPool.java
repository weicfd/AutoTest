package parser;

import utils.entity.Obj;
import utils.entity.Operation;
import utils.entity.ParamPair;
import utils.entity.Service;

import javax.xml.crypto.Data;
import java.util.*;

/**
 * Created by tangmh on 17/12/22.
 */
public class DataPool {

    // 单例模式
    private static DataPool instance;

    public static DataPool getInstance() {
        if (instance == null) {
            instance = new DataPool();
        }
        return instance;
    }

    // TODO: 17/12/29 大改
//    private boolean mode; // 数据池的模式: 包括普通模式(false)和休眠模式(true),暂时先实现普通模式
    private Map<String, Service> servicePool;
    private Map<String, Obj> objectPool;
    private List<Operation> operationPool;
    private Map<String, List<String>> valPool;
    private Map<String, Integer> valCursor;


    private DataPool() {
//        mode = config.SysConfig.DATAPOOL_SOFT_PARAM;
        servicePool = new HashMap<>();
        objectPool = new HashMap<>();
        operationPool = new ArrayList<>();
        valPool = new HashMap<>();
        valCursor = new HashMap<>();
    }

    // -----------service-----------
    public void addService(Service service) {
        if (!servicePool.containsKey(service.getId())) {
            servicePool.put(service.getId(), service);
        }
    }

    public Service getService(String id) {
        return servicePool.get(id);
    }

    // -----------obj-----------
    public void clearObjects() {
        objectPool.clear();
    }

    // -----------operation-----------
    public List<Operation> getOperations() {
        return operationPool;
    }
    public void  clearOperations() {
        operationPool.clear();
    }

    public void add(Operation op) {
        operationPool.add(op);
    }

    // -----------data-----------
    public void addData(String attr, List<String> values) {
        if (valPool.containsKey(attr)) {
            valPool.get(attr).addAll(values);
        } else {
            valPool.put(attr, values);
            valCursor.put(attr, 0);
        }
    }

    public String getAttrVal(String attr) {
        int cur = valCursor.get(attr);
        List<String> vals = valPool.get(attr);
        if (cur == vals.size() -1) valCursor.put(attr, 0);
        else valCursor.put(attr, cur + 1);
        return vals.get(cur);
    }

    public int countDataNo(String attr) {
        return valPool.get(attr) == null ? 0 : valPool.get(attr).size();
    }
//    class DataNode {
//        String val;
//        Set<DataNode> childs;
//
//        public DataNode(String val) {
//            this.val = val;
//            childs = null;
//        }
//    }

}
