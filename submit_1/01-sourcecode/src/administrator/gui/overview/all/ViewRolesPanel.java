package administrator.gui.overview.all;
import administrator.dao.DBObject;
import administrator.dbHandler.IDBAHandler;
import administrator.dbHandler.roleAndUser.IRoleHandler;
import administrator.dbHandler.table.ITableHandler;
import administrator.gui.overview.viewDetails.ViewRoleInsideFrame;
import administrator.gui.privileges.RolePrivilegeFrame;
import administrator.gui.table.cells.ButtonCell;
import administrator.gui.table.cells.ICell;
import administrator.gui.table.cells.LabelCell;

/**
 * administrator.gui
 * Created by NhatLinh - 19127652
 * Date 3/23/2022 - 8:44 AM
 * Description: ...
 */
public class ViewRolesPanel extends OverviewPanel {

    private final RolePrivilegeFrame privilegeFrame;
    public ViewRolesPanel(IRoleHandler roleHandler, IDBAHandler userHandler, ITableHandler tableHandler) {
        super(roleHandler,
                new ICell[] {
                    new LabelCell("Role name"),
                    new LabelCell("Users"),
                    new LabelCell("View/Grant/Revoke permissions"),
                    new LabelCell("Drop role")},
                new ViewRoleInsideFrame("View users and roles"),
                tableHandler
        );
        privilegeFrame = new RolePrivilegeFrame(userHandler, roleHandler, tableHandler);
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
