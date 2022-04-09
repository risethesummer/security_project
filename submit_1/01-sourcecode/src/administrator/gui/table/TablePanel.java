package administrator.gui.table;

import administrator.gui.table.cells.ICell;
import administrator.gui.table.row.IRow;
import administrator.gui.table.row.NColumnsPanel;
import administrator.gui.table.row.TitleRowPanel;
import common.gui.ScrollablePanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * administrator.gui.customTable
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 3:41 PM
 * Description: ...
 */
public class TablePanel extends ScrollablePanel implements ITablePanel  {

    protected final NColumnsPanel title;
    protected final List<IRow> currentRows = new ArrayList<>();

    public TablePanel(ICell[] titleCells)
    {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setScrollableWidth(ScrollableSizeHint.FIT);
        title = new TitleRowPanel(titleCells);
        title.setMaximumSize(new Dimension(Integer.MAX_VALUE, title.getPreferredSize().height));
        add(title);
    }

    public NColumnsPanel getTitleRow()
    {
        return title;
    }
    //public NRowsPanel getRows() {
        //return rows;
    //}

    @Override
    public void addRow(IRow row)
    {
        currentRows.add(row);
        SwingUtilities.invokeLater(() ->
                add(row.getComponent()));
    }

/*    public void addRows(Collection<IRow> rows)
    {
        for (IRow row : rows)
            addRow(row);
    }*/


    @Override
    public void swapRow(int index, int next) {
        int finalNext = ++next;
        int finalIndex = ++index;
        Component t = getComponent(finalIndex);
        SwingUtilities.invokeLater(() ->
        {
            super.remove(finalIndex);
            add(t, finalNext);
        });
    }

    @Override
    public void clearRows()
    {
        currentRows.clear();
        SwingUtilities.invokeLater(() -> {
            removeAll();
            add(title);
        });

    }

    @Override
    public void deleteRow(String header)
    {
        int rowIndex = getRow(header);
        if (rowIndex != -1)
            SwingUtilities.invokeLater(() -> remove(rowIndex));
    }

    @Override
    public int getRow(String header)
    {
        for (int i = 0; i < currentRows.size(); i++)
            if (currentRows.get(i).getHeader().equals(header))
                return i;
        return -1;
    }

    @Override
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

    @Override
    public void showRows()
    {
        for (IRow row : currentRows)
        {
            SwingUtilities.invokeLater(() ->
                    row.getComponent().setVisible(true));
        }
    }

    @Override
    public Component getAddComponent() {
        return new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    /*@Override
    public void updateUI()
    {
        super.updateUI();
    }*/
    public void remove(Component t)
    {
        if (title == t)
            return;
        super.remove(t);
    }
    /*public int getComponentCount(){
        return super.getComponentCount();
    }
    public Component getComponent(int index)
    {
        return super.getComponent(index);
    }*/

/*
    public void remove(int finalIndex){

        super.remove(finalIndex + 1);
    }

    public void addRow(IRow c, int next)
    {
        currentRows.add(c);
        SwingUtilities.invokeLater(() ->
                add(c.getComponent(), next));
    }
*/

    public Component[] getComponents()
    {
        Component[] original = super.getComponents();
        Component[] cs = new Component[original.length - 1];
        for (int i = 1; i < original.length; i++)
            cs[i - 1] = original[i];
        return cs;
    }
}
