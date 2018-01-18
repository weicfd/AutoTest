package utils.entity;

/**
 * Created by tangmh on 18/1/19.
 */
public class Service {
    String id;
    String name;
    String base;

    public Service(String id, String name, String base) {
        this.id = id;
        this.name = name;
        this.base = base;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBase() {
        return base;
    }
}
