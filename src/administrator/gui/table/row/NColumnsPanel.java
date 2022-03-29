package administrator.gui.table.row;

import administrator.gui.table.cells.ICell;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * administrator.gui
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 1:15 PM
 * Description: ...
 */
public abstract class NColumnsPanel extends JPanel implements IRow {

    public NColumnsPanel(int n)
    {
        super(new GridLayout(1, n));
    }

    public NColumnsPanel(Collection<ICell> cells)
    {
        this(cells.size());
        for (ICell cell : cells)
            add(cell.getComponent());
    }

    public NColumnsPanel(ICell[] cells)
    {
        this(cells.length);
        for (ICell cell : cells)
            add(cell.getComponent());
    }

    public void addCell(ICell... cells)
    {
        for (ICell cell : cells)
            add(cell.getComponent());
    }

    @Override
    public abstract String getHeader();

    @Override
    public abstract Component getComponent();
}
