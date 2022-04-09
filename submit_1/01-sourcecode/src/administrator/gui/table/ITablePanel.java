package administrator.gui.table;

import administrator.gui.table.row.IRow;
import java.awt.*;

/**
 * administrator.gui.table
 * Created by NhatLinh - 19127652
 * Date 3/30/2022 - 9:54 AM
 * Description: ...
 */
public interface ITablePanel {
    void addRow(IRow row);
    void swapRow(int index, int next);
    //void addRow(IRow c, int next);
    void clearRows();
    void deleteRow(String header);
    void remove(Component t);
    //void remove(int finalIndex);
    Component[] getComponents();
    //Component getComponent();
    Component getAddComponent();
    int getRow(String header);
    void hideRows(String exception);
    void showRows();
    void updateUI();
}
