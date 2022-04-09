package administrator.dao.permissions;

import java.util.List;

/**
 * administrator.dao
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 5:21 PM
 * Description: ...
 */
public class DetailedActionPermission extends ActionPermission {

    private final List<Boolean> columns;

    public DetailedActionPermission(boolean granted, boolean wgo, List<Boolean> columns) {
        super(granted, wgo);
        this.columns = columns;
    }

    public List<Boolean> getColumns() {
        return columns;
    }
}
