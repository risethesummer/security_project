package administrator.gui.overview;

import administrator.dbHandler.IDBAHandler;
import administrator.gui.table.cells.ICell;
import administrator.gui.table.cells.LabelCell;

/**
 * administrator.gui
 * Created by NhatLinh - 19127652
 * Date 2/21/2022 - 9:07 PM
 * Description: ...
 */

public class ViewUsersPanel extends OverviewPanel {

    public ViewUsersPanel(IDBAHandler dbHandler)
    {
        super(dbHandler,
                new ICell[]{
                        new LabelCell("User name"),
                        new LabelCell("Roles"),
                        new LabelCell("Privileges"),
                        new LabelCell("Drop user")
                },
                new ViewInsideObjectsFrame("View roles")
        );
    }
}
