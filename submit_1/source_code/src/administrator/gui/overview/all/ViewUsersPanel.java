package administrator.gui.overview.all;

import administrator.dao.DBObject;
import administrator.dbHandler.IDBAHandler;
import administrator.dbHandler.roleAndUser.IRoleHandler;
import administrator.dbHandler.table.ITableHandler;
import administrator.gui.overview.viewDetails.ViewInsideObjectsFrame;
import administrator.gui.privileges.UserPrivilegeFrame;
import common.gui.table.cells.ButtonCell;
import common.gui.table.cells.ICell;
import common.gui.table.cells.LabelCell;

/**
 * administrator.gui
 * Created by NhatLinh - 19127652
 * Date 2/21/2022 - 9:07 PM
 * Description: ...
 */

public class ViewUsersPanel extends OverviewPanel {

    private final UserPrivilegeFrame privilegeFrame;
    public ViewUsersPanel(IDBAHandler userHandler, IRoleHandler roleHandler, ITableHandler tableHandler)
    {
        super(userHandler,
                new ICell[]{
                        new LabelCell("User name"),
                        new LabelCell("Roles"),
                        new LabelCell("Privileges"),
                        new LabelCell("Drop user")
                },
                new ViewInsideObjectsFrame("View roles"),
                tableHandler
        );

        privilegeFrame = new UserPrivilegeFrame(userHandler, roleHandler, tableHandler);
    }

    protected ICell[] getCell(DBObject obj, ButtonCell view)
    {
        return new ICell[] {
                new LabelCell(obj.getName()),
                view,
                new ButtonCell("Permissions", () -> privilegeFrame.setVisible(obj.getName())),
                new ButtonCell("Drop", () -> dropObject(obj.getName()))
        };
    }


    public void dispose()
    {
        privilegeFrame.dispose();
        super.dispose();
    }
}
