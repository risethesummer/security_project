package administrator.gui.privileges.userRole;

import administrator.dbHandler.IDBAHandler;
import administrator.dbHandler.roleAndUser.IRoleHandler;

/**
 * administrator.gui.privileges.userRole
 * Created by NhatLinh - 19127652
 * Date 4/9/2022 - 6:35 PM
 * Description: ...
 */
public class GrantRevokeRolePanel extends GrantRevokePanel{
    public GrantRevokeRolePanel(IRoleHandler roleHandler) {
        super(roleHandler, roleHandler);
    }

    public boolean grant(String roleName)
    {
        return roleHandler.grant(name, roleName);
    }

    public boolean revoke(String roleName)
    {
        return roleHandler.revoke(name, roleName);
    }
}
