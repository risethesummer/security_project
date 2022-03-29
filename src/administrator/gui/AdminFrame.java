package administrator.gui;

import administrator.dbHandler.IDBAHandler;
import administrator.dbHandler.table.ITableHandler;
import administrator.gui.create.objects.CreateObjectTab;
import administrator.gui.overview.ViewRolesPanel;
import administrator.gui.overview.ViewTablesPanel;
import administrator.gui.overview.ViewUsersPanel;

import javax.swing.*;

/**
 * administrator
 * Created by NhatLinh - 19127652
 * Date 2/21/2022 - 8:14 PM
 * Description: ...
 */
public class AdminFrame extends JFrame {

    private final ViewUsersPanel viewUsers;
    private final ViewRolesPanel viewRoles;
    private final ViewTablesPanel viewTables;
    private final IDBAHandler userHandler;
    private final IDBAHandler roleHandler;

    public AdminFrame(IDBAHandler userHandler, IDBAHandler roleHandler, ITableHandler tableHandler)
    {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        this.userHandler = userHandler;
        this.roleHandler = roleHandler;
        tabbedPane.add("Manage users", viewUsers = new ViewUsersPanel(userHandler));
        tabbedPane.add("Manage roles", viewRoles = new ViewRolesPanel(roleHandler));
        tabbedPane.add("Manage tables", viewTables = new ViewTablesPanel(tableHandler));
        tabbedPane.add("Create objects", new CreateObjectTab(userHandler, roleHandler, null));
        getContentPane().add(tabbedPane);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                viewUsers.dispose();
                viewRoles.dispose();
                viewTables.dispose();
            }
        });
        setVisible(true);
        pack();
    }

}
