package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangmh on 17/12/22.
 */
public class DataPool {
    private boolean mode; // 数据池的模式: 包括普通模式(false)和休眠模式(true),暂时先实现普通模式
    private Map<Long, Map<String, String>> data;

    public DataPool() {
        mode = config.SysConfig.DATAPOOL_SOFT_PARAM;
        data = new HashMap<>();
    }

    public void clear(){
        data.clear();
    }
}
