package administrator.dao.permissions;

import java.util.List;
import java.util.Map;

/**
 * administrator.dao.permissions
 * Created by NhatLinh - 19127652
 * Date 4/2/2022 - 12:26 PM
 * Description: ...
 */
public record GeneralPermission (List<String> properties, Map<PermissionType, ActionPermission> permissions){

}
