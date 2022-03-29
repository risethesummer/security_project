package administrator.dbHandler;

import administrator.dao.DBObject;

import java.util.Collection;
import java.util.List;

/**
 * administrator.dbHandler
 * Created by NhatLinh - 19127652
 * Date 3/24/2022 - 10:24 PM
 * Description: ...
 */
public interface IDBAHandler {
    List<DBObject> getObjects();
    boolean createObject(DBObject object);
    boolean checkNameExists(String name);
    boolean dropObject(String name);
    public Collection<DBObject> getInsideObjects(String name);
}
