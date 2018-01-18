package utils.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by tangmh on 17/12/22.
 */
public class Operation {
    String type;
    String name;
    String path;
    String locator;
    Set<String> attrSet;

    public Operation(String type, String name, String path) {
        this.type = type;
        this.name = name;
        this.path = path;
        locator = null;
        attrSet = new HashSet<>();
    }

    public String getType() {
        return type;
    }

    public String getLocator() {
        return locator;
    }

    public String getName() {
        return name;
    }

    public Set<String> getAttrs() {
        return attrSet;
    }

    public void addAttr(String attr) {
        attrSet.add(attr);
    }
}
