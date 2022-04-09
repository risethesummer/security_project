package administrator.gui.privileges.userRole;

import administrator.dbHandler.roleAndUser.IRoleHandler;

import java.util.Collection;

/**
 * administrator.gui.privileges.viewDB
 * Created by NhatLinh - 19127652
 * Date 4/1/2022 - 12:36 AM
 * Description: ...
 */
public class GrantRevokeRoleRolePanel extends GrantRevokeRolePanel {

    public GrantRevokeRoleRolePanel(IRoleHandler roleHandler) {
        super(roleHandler);
    }

    @Override
    protected Collection<String> getInsideObjs()
    {
        return roleHandler.getInsideRole(name);
    }

    @Override
    protected Collection<String> getOutsideObjs()
    {
        return roleHandler.getOutsideRole(name);
    }
}
