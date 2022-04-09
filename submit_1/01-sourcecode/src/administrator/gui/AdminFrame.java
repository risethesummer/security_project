package administrator.gui;

import administrator.dbHandler.IDBAHandler;
import administrator.dbHandler.roleAndUser.IRoleHandler;
import administrator.dbHandler.table.ITableHandler;
import administrator.gui.create.objects.CreateObjectTab;
import administrator.gui.overview.all.ViewRolesPanel;
import administrator.gui.overview.all.ViewTablesPanel;
import administrator.gui.overview.all.ViewUsersPanel;
import common.gui.DisposableFrame;

import javax.swing.*;

/**
 * administrator
 * Created by NhatLinh - 19127652
 * Date 2/21/2022 - 8:14 PM
 * Description: ...
 */
public class AdminFrame extends DisposableFrame {

    private final ViewUsersPanel viewUsers;
    private final ViewRolesPanel viewRoles;
    private final ViewTablesPanel viewTables;
    private final ViewTablesPanel viewViews;
    private final IDBAHandler userHandler;
    private final IDBAHandler roleHandler;

    public AdminFrame(Runnable onClose, IDBAHandler userHandler, IRoleHandler roleHandler, ITableHandler tableHandler, IDBAHandler viewHandler)
    {
        super(onClose);
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        this.userHandler = userHandler;
        this.roleHandler = roleHandler;
        tabbedPane.add("View users", viewUsers = new ViewUsersPanel(userHandler, roleHandler, tableHandler));
        tabbedPane.add("View roles", viewRoles = new ViewRolesPanel(roleHandler, userHandler, tableHandler));
        tabbedPane.add("View tables", viewTables = new ViewTablesPanel(tableHandler));
        tabbedPane.add("View views", viewViews = new ViewTablesPanel(viewHandler));
        tabbedPane.add("Create objects", new CreateObjectTab(userHandler, roleHandler, tableHandler));
        getContentPane().add(tabbedPane);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        //pack();
    }

    @Override
    public void dispose()
    {
        viewUsers.dispose();
        viewRoles.dispose();
        viewTables.dispose();
        viewViews.dispose();
        onDispose.run();
        super.dispose();
    }
}
