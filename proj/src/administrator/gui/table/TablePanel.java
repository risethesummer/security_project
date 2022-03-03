package administrator.gui.table;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * administrator.gui.customTable
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 3:41 PM
 * Description: ...
 */
public class TablePanel extends JPanel {

    private final NColumnsPanel title;
    private final NRowsPanel rows = new NRowsPanel();

    public TablePanel(String[] titles)
    {
        super(new BorderLayout());
        List<ICell> titleCells = new ArrayList<>(titles.length);
        for (String t : titles)
            titleCells.add(new TextCell(t));
        title = new NColumnsPanel(titleCells);
        JScrollPane scrollPane = new JScrollPane(rows);
        add(title, BorderLayout.PAGE_START);
        add(scrollPane, BorderLayout.CENTER);
    }

    public NRowsPanel getRows() {
        return rows;
    }
}
