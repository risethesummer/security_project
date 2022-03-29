package administrator.gui.table;

import administrator.gui.table.cells.ICell;
import administrator.gui.table.row.NColumnsPanel;
import administrator.gui.table.row.NRowsPanel;
import administrator.gui.table.row.TitleRowPanel;

import javax.swing.*;
import java.awt.*;

/**
 * administrator.gui.customTable
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 3:41 PM
 * Description: ...
 */
public class TablePanel extends JPanel {

    private final NColumnsPanel title;
    private final NRowsPanel rows = new NRowsPanel();

    public TablePanel(ICell[] titleCells)
    {
        super(new BorderLayout());
        title = new TitleRowPanel(titleCells);
        JScrollPane scrollPane = new JScrollPane(rows, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(title, BorderLayout.PAGE_START);
        add(scrollPane, BorderLayout.CENTER);
    }

    public NColumnsPanel getTitleRow()
    {
        return title;
    }

    public NRowsPanel getRows() {
        return rows;
    }
}
