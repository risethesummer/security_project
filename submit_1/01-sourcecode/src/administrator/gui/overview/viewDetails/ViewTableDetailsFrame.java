package administrator.gui.overview.viewDetails;

import administrator.dao.DBObject;
import administrator.dao.table.property.NumberProperty;
import administrator.dao.table.property.Property;
import administrator.dao.table.property.SizedProperty;
import administrator.gui.overview.DBObjectRowPanel;
import administrator.gui.table.TablePanel;
import administrator.gui.table.cells.*;
import administrator.gui.table.row.NColumnsPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * administrator.gui.overview
 * Created by NhatLinh - 19127652
 * Date 3/27/2022 - 4:06 PM
 * Description: ...
 */
public class ViewTableDetailsFrame extends JFrame implements IShowDetails {

    private final TablePanel tablePanel = new TablePanel(new ICell[]{
            new LabelCell("Name"),
            new LabelCell("Type"),
            new LabelCell("Length"),
            new LabelCell("Precision"),
            new LabelCell("Primary Key"),
            new LabelCell("Unique"),
            new LabelCell("Foreign Key"),
            new LabelCell("Nullable"),
            new LabelCell("References"),
    });

    public ViewTableDetailsFrame(String title)
    {
        super(title);
        getContentPane().add(tablePanel);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    @Override
    public void setObjects(String name, Collection<DBObject> objs) {
        setTitle(name);
        tablePanel.clearRows();
        for (DBObject obj : objs)
        {
            Property property = (Property)obj;
            int len;
            try
            {
                len = ((SizedProperty)property).getLength();
            }
            catch (Exception e) {
                len = 0;
            }

            int precision;
            try
            {
                precision = ((NumberProperty)property).getPrecision();
            }
            catch (Exception e) {
                precision = 0;
            }

            String ref = property.getReferences() == null ? "No references" : property.getReferences().toString();

            NColumnsPanel row = new DBObjectRowPanel(null, new ICell[]{
                    new LabelCell(property.getName()),
                    new LabelCell(property.getColumnType()),
                    new LabelCell(String.valueOf(len)),
                    new LabelCell(String.valueOf(precision)),
                    new CheckBoxCell(property.isPk()),
                    new CheckBoxCell(property.isUnique()),
                    new CheckBoxCell(property.isFk()),
                    new CheckBoxCell(property.isNullable()),
                    new LabelCell(ref),
            });
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
            tablePanel.addRow(row);
        }
        SwingUtilities.invokeLater(tablePanel::updateUI);
    }
}
