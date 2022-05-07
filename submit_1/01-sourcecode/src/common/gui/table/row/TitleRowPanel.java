package common.gui.table.row;

import common.gui.table.cells.ICell;

import java.awt.*;
import java.util.Collection;

/**
 * common.gui.table
 * Created by NhatLinh - 19127652
 * Date 3/12/2022 - 11:21 PM
 * Description: ...
 */
public class TitleRowPanel extends NColumnsPanel{

    public TitleRowPanel(Collection<ICell> cells) {
        super(cells);
    }

    public TitleRowPanel(ICell[] cells) {
        super(cells);
    }

    @Override
    public String getHeader() {
        return null;
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
