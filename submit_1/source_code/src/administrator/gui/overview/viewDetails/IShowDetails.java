package administrator.gui.overview.viewDetails;

import administrator.dao.DBObject;

import java.util.Collection;

/**
 * administrator.gui.overview
 * Created by NhatLinh - 19127652
 * Date 3/27/2022 - 4:00 PM
 * Description: ...
 */
public interface IShowDetails {
    void setVisible(boolean visible);
    void setObjects(String name, Collection<DBObject> objs);
    void dispose();
}
