package administrator.dao;

import java.util.Map;

/**
 * administrator.dao
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 5:21 PM
 * Description: ...
 */
public record Permission(DBObject object, Map<PermissionType, Boolean> checkPermissions) {
}
