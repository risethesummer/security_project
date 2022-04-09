package administrator.gui.table.row;

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

    public void addRow(IRow row)
    {
        currentRows.add(row);
        SwingUtilities.invokeLater(() ->
            add(row.getComponent()));
    }

    public void clearRows()
    {
        currentRows.clear();
        SwingUtilities.invokeLater(this::removeAll);
    }

    public void deleteRow(String header)
    {
        int rowIndex = getRow(header);
        if (rowIndex != -1)
            SwingUtilities.invokeLater(() -> remove(rowIndex));
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
            String header = row.getHeader();
            if (header.length() > exception.length())
                header = header.substring(0, exception.length());
            if (header.equalsIgnoreCase(exception))
                SwingUtilities.invokeLater(() -> row.getComponent().setVisible(true));
            else
                SwingUtilities.invokeLater(() -> row.getComponent().setVisible(false));
        }
    }

    public void showRows()
    {
        for (IRow row : currentRows)
        {
            SwingUtilities.invokeLater(() ->
                row.getComponent().setVisible(true));
        }
    }
}
