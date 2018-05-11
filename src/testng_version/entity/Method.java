package testng_version.entity;

import java.util.List;
import java.util.Map;

public class Method {
    final int mADD = 1, mUPDATE = 2, mDELETE = 3, mFIND = 4, mUNKNOWN = 0;
    String methodName;
    int mType = mUNKNOWN;
    Map<String,String> dataCodeMap;
    List<String> targets;

    public Method(String methodName, int mType, Map<String, String> dataCodeMap, List<String> targets) {
        this.methodName = methodName;
        this.mType = mType;
        this.dataCodeMap = dataCodeMap;
        this.targets = targets;
    }

    public String getMethodName() {
        return methodName;
    }
    public int getmType() {
        return mType;
    }

    public Map<String,String> getDataCodeMap() {
        return dataCodeMap;
    }

    public List<String> getTargets() {
        return targets;
    }
}
