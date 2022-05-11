package administrator.gui.create.objects;

import administrator.dbHandler.IDBAHandler;
import administrator.dbHandler.table.ITableHandler;
import administrator.gui.create.table.CreateTablePanel;

import javax.swing.*;

/**
 * administrator.gui.create
 * Created by NhatLinh - 19127652
 * Date 3/26/2022 - 1:07 PM
 * Description: ...
 */
public class CreateObjectTab extends JTabbedPane {

    public CreateObjectTab(IDBAHandler userHandler, IDBAHandler roleHandler, ITableHandler tableHandler)
    {
        add("User", new CreateUserPanel(userHandler, roleHandler));
        add("Role", new CreateRoleTable(roleHandler, userHandler));
        add("Table", new CreateTablePanel(tableHandler));
    }
}
