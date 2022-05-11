package administrator.gui.privileges;

import administrator.dbHandler.IDBAHandler;
import administrator.dbHandler.roleAndUser.IRoleHandler;
import administrator.dbHandler.table.ITableHandler;
import administrator.gui.privileges.table.SearchTablePanel;
import administrator.gui.privileges.userRole.GrantRevokePanel;

import javax.swing.*;

/**
 * administrator.gui
 * Created by NhatLinh - 19127652
 * Date 3/14/2022 - 3:40 PM
 * Description: ...
 */
public class UserPrivilegeFrame extends JFrame implements IPrivilege {

    protected String name = "";
    protected GrantRevokePanel rolePrivilege;
    protected SearchTablePanel tablePrivilege;
    protected  JTabbedPane tab = new JTabbedPane(JTabbedPane.LEFT);

    public UserPrivilegeFrame(IDBAHandler userHandler, IRoleHandler roleHandler, ITableHandler tableHandler)
    {
        rolePrivilege = new GrantRevokePanel(userHandler, roleHandler);
        tablePrivilege = new SearchTablePanel(tableHandler);
        tab.add("Role privilege", rolePrivilege);
        tab.add("Table privilege", tablePrivilege);
        getContentPane().add(tab);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void setVisible(String name)
    {
        this.name = name;
        rolePrivilege.setUserName(name);
        tablePrivilege.setUserName(name);
        setTitle(name);
        setVisible(true);
    }
}
