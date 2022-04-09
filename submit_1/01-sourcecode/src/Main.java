import administrator.dao.permissions.ActionPermission;
import administrator.dao.permissions.DetailedActionPermission;
import administrator.dao.permissions.GeneralPermission;
import administrator.dao.permissions.PermissionType;
import administrator.dbHandler.roleAndUser.DBARoleHandler;
import administrator.dbHandler.roleAndUser.DBAUserHandler;
import administrator.dbHandler.table.TableHandler;
import administrator.dbHandler.table.ViewHandler;
import administrator.gui.AdminFrame;
import administrator.gui.privileges.RolePrivilegeFrame;
import administrator.gui.privileges.UserPrivilegeFrame;
import administrator.gui.privileges.table.SearchTablePanel;
import administrator.gui.privileges.table.TablePrivilegePanel;
import common.handler.DBHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * PACKAGE_NAME
 * Created by NhatLinh - 19127652
 * Date 2/16/2022 - 10:17 AM
 * Description: ...
 */
public class Main {


    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
