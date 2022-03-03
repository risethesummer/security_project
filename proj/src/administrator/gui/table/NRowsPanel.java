package administrator.gui.table;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * administrator.gui.customTable
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 1:32 PM
 * Description: ...
 */
public class NRowsPanel extends JPanel {

    private final List<IRow> currentRows = new ArrayList<>();

    public NRowsPanel()
    {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    public void addRows(IRow[] rows)
    {
        for (IRow row : rows)
        {
            currentRows.add(row);
            add(row.getComponent());
        }
    }

    public void clearRows()
    {
        currentRows.clear();
        removeAll();
    }

    public void deleteRow(String header)
    {
        int rowIndex = getRow(header);
        if (rowIndex != -1)
        {
            remove(currentRows.get(rowIndex).getComponent());
            currentRows.remove(rowIndex);
        }
    }

    public int getRow(String header)
    {
        for (int i = 0; i < currentRows.size(); i++)
            if (currentRows.get(i).getHeader().equals(header))
                return i;
        return -1;
    }

    public void hideRows(String exception)
    {
        for (IRow row : currentRows)
        {
            if (!row.getHeader().equals(exception))
                row.getComponent().setVisible(false);
        }
    }

    public void showRows()
    {
        for (IRow row : currentRows)
            row.getComponent().setVisible(true);
    }

    public void sortRows()
    {
        if (!currentRows.isEmpty())
        {
            currentRows.sort(IRow::compare);
            removeAll();
            for (IRow row : currentRows)
                add(row.getComponent());
        }
    }
}
