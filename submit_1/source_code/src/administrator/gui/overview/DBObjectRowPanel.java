package administrator.gui.overview;

import administrator.dao.DBObject;
import common.gui.table.cells.ICell;
import common.gui.table.row.NColumnsPanel;

import java.awt.*;

/**
 * common.gui.table
 * Created by NhatLinh - 19127652
 * Date 3/12/2022 - 11:16 PM
 * Description: ...
 */
public class DBObjectRowPanel extends NColumnsPanel {

    private final DBObject shownObject;

    public DBObjectRowPanel(DBObject shownObject, ICell[] cells) {
        super(cells);
        this.shownObject = shownObject;
    }

    @Override
    public String getHeader() {
        return shownObject.getName();
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
