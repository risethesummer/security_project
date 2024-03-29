package administrator.gui.overview.all;

import administrator.dao.DBObject;
import administrator.dbHandler.IDBAHandler;
import administrator.gui.overview.viewDetails.ViewTableDetailsFrame;
import common.gui.table.cells.ButtonCell;
import common.gui.table.cells.ICell;
import common.gui.table.cells.LabelCell;

/**
 * administrator.gui.overview
 * Created by NhatLinh - 19127652
 * Date 3/27/2022 - 3:36 PM
 * Description: ...
 */
public class ViewTablesPanel extends OverviewPanel
{
    public ViewTablesPanel(IDBAHandler handler)
    {
        super(handler,
                new ICell[]{
                    new LabelCell("Name"),
                    new LabelCell("Properties"),
                    new LabelCell("Drop")},
                new ViewTableDetailsFrame("Table details"),
                null
        );
    }


    protected ICell[] getCell(DBObject obj, ButtonCell view)
    {
        return new ICell[] {
                new LabelCell(obj.getName()),
                view,
                new ButtonCell("Drop", () -> dropObject(obj.getName()))
        };
    }
}
