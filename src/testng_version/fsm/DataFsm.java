package testng_version.fsm;

import testng_version.entity.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFsm {
    int cursor;
//    final int sStart = 1, sStandard = 2, sTest = 3, sEnd = 4;
    final int mADD = 1, mUPDATE = 2, mDELETE = 3, mFIND = 4, mUNKNOWN = 0;
    Map<Integer, Integer> usedCodeList; // half-code, data_no
    List<Method> methods;
    int num, m_p_star;
    Method m, m_f, m_last;

    private DataFsm() {
        usedCodeList = new HashMap<>();
    }

    private static DataFsm dataFsm = new DataFsm();

    public static DataFsm getDataFsm() {
        return dataFsm;
    }

    public void init(List<Method> methods) {
        usedCodeList.clear();
        this.methods = methods;
        num = methods.size();
        m_f = methods.get(num - 1);
        m = methods.get(cursor);
        m_p_star = m.getLocator();
        m_last = null;
        cursor = 0;
    }

    public void next() {
        cursor++;
        m = methods.get(cursor);
        m_last = null;
        m_p_star = m.getLocator();
        if (methods.size() < 2) System.err.println("methods len error");
        // find the m'
        for (int i = num - 3; i >= 0; i--) {
            if (methods.get(i).getDataCodeMap().containsKey(m_p_star))
                m_last = methods.get(i);
        }
    }

    public int getData(int dataCode) {
        int res;

        if (m_last == null) {
            // new entity
            res = usedCodeList.getOrDefault(dataCode, 0) + 1; // 从1开始标号
            usedCodeList.put(dataCode, res);
            return res;
        } else {
            switch (m.getmType()) {
                case mADD:
                    res = usedCodeList.getOrDefault(dataCode, 0) + 1;
                    usedCodeList.put(dataCode, res);
                    return res;
                case mUPDATE:
                    if (dataCode == m_p_star) return usedCodeList.get(dataCode);
                    else {
                        res = usedCodeList.getOrDefault(dataCode, 0) + 1;
                        usedCodeList.put(dataCode, res);
                        return res;
                    }
                case mDELETE:
                    if (m_last.getmType() == mDELETE) {
                        res = usedCodeList.get(dataCode) - 1;
                        usedCodeList.put(dataCode, res);
                        return res;
                    } else {
                        return usedCodeList.get(dataCode);
                    }
                case mFIND:
                    return usedCodeList.get(dataCode);
                default:
                    return 0;

            }
        }

//        switch (mType) {
//            case mADD:
//
//                break;
//            case mUPDATE:
//                break;
//            case mDELETE:
//                break;
//            case mFIND:
//                break;
//            case mUNKNOWN:
//                System.err.println("unknown method type:" + dataCode);
//                break;
//        }

    }
}
