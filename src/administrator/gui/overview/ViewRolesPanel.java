package administrator.gui.overview;
import administrator.dbHandler.IDBAHandler;
import administrator.gui.table.cells.ICell;
import administrator.gui.table.cells.LabelCell;

/**
 * administrator.gui
 * Created by NhatLinh - 19127652
 * Date 3/23/2022 - 8:44 AM
 * Description: ...
 */
public class ViewRolesPanel extends OverviewPanel {

    public ViewRolesPanel(IDBAHandler dbHandler) {
        super(dbHandler,
                new ICell[] {
                    new LabelCell("Role name"),
                    new LabelCell("Users"),
                    new LabelCell("View/Grant/Revoke permissions"),
                    new LabelCell("Drop role")},
                new ViewInsideObjectsFrame("View users")
        );
    }
}
