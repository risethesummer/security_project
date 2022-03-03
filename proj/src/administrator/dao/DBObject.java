package administrator.dao;

/**
 * administrator.dao
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 5:22 PM
 * Description: ...
 */
public class DBObject {
    protected final String name;
    protected final DBObjectType type;

    public DBObject(String name, DBObjectType type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public DBObjectType getType() {
        return type;
    }
}
