package utils.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangmh on 17/12/29.
 */
public class Obj {
    int id;
    String type;
    Map<String, ParamPair> attrs;

    public Obj(int id, String type, Map<String, ParamPair> attrs) {
        this.id = id;
        this.type = type;
        this.attrs = attrs;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Map<String, ParamPair> getAttrs() {
        return attrs;
    }

    public String getAttr(String attr) {
        return attrs.get(attr).getValue();
    }
    public void putAttr(String attr, String nVal) {
        attrs.put(attr, new ParamPair(attr, nVal));
    }
}
