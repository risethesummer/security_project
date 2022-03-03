package administrator.gui.table;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;

/**
 * administrator.gui
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 1:15 PM
 * Description: ...
 */
public class NColumnsPanel extends JPanel {

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
}
