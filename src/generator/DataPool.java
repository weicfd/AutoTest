package generator;

import utils.entity.Obj;
import utils.entity.Operation;
import utils.entity.ParamPair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by tangmh on 17/12/22.
 */
public class DataPool {
    // TODO: 17/12/29 大改
    private boolean mode; // 数据池的模式: 包括普通模式(false)和休眠模式(true),暂时先实现普通模式
    private List<Obj> dataParent;
    private List<DataChild> dataChild;


    public DataPool() {
        mode = config.SysConfig.DATAPOOL_SOFT_PARAM;
        dataParent = new LinkedList<>();
        dataChild = new LinkedList<>();
    }

    public void clear(){
        // TODO: 17/12/29
    }

    public void add(Operation op, List<ParamPair> params) {
    }

    public void modify(Operation op, ParamPair locator) {
    }

    public void delete(Operation op, ParamPair locator) {
    }

    public void find(Operation op, ParamPair locator) {
    }

    private class DataChild {

    };
}
