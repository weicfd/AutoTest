package utils.entity;

/**
 * Created by tangmh on 17/12/29.
 */
public class ParamPair {
    String attr;
    String value;

    public ParamPair(String attr, String value) {
        this.attr = attr;
        this.value = value;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
