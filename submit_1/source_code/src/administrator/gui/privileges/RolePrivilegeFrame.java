package administrator.gui.privileges;

import administrator.dbHandler.IDBAHandler;
import administrator.dbHandler.roleAndUser.IRoleHandler;
import administrator.dbHandler.table.ITableHandler;
import administrator.gui.privileges.userRole.GrantRevokePanel;
import administrator.gui.privileges.userRole.GrantRevokeRolePanel;
import administrator.gui.privileges.userRole.GrantRevokeRoleRolePanel;

/**
 * administrator.gui.privileges
 * Created by NhatLinh - 19127652
 * Date 4/1/2022 - 12:21 AM
 * Description: ...
 */
public class RolePrivilegeFrame extends UserPrivilegeFrame implements IPrivilege{

    protected final GrantRevokePanel userPrivilege;

    public RolePrivilegeFrame(IDBAHandler userHandler, IRoleHandler roleHandler, ITableHandler tableHandler) {
        super(userHandler, roleHandler, tableHandler);
        tab.remove(0);
        rolePrivilege = new GrantRevokeRoleRolePanel(roleHandler);
        userPrivilege = new GrantRevokeRolePanel(roleHandler);
        tab.add(userPrivilege, 0);
        tab.setTitleAt(0, "User privilege");
        tab.add(rolePrivilege, 1);
        tab.setTitleAt(1, "Role privilege");
        //tab.add("User privilege", userPrivilege);
    }

    public void setVisible(String name)
    {
        super.setVisible(name);
        userPrivilege.setUserName(name);
    }
}
