package administrator.gui.create.table;

import administrator.dao.table.Table;
import administrator.dao.table.property.Property;
import administrator.dbHandler.IDBAHandler;
import administrator.dbHandler.table.ITableHandler;
import administrator.gui.table.TablePanel;
import administrator.gui.table.cells.ICell;
import administrator.gui.table.cells.LabelCell;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * administrator.gui.create
 * Created by NhatLinh - 19127652
 * Date 3/26/2022 - 10:31 PM
 * Description: ...
 */
public class CreateTablePanel extends JPanel {

    private final IDBAHandler handler;
    private final JTextField tableNameField = new JTextField();
    private final TablePanel propertyTable = new TablePanel(new ICell[]{
            new LabelCell("Name"),
            new LabelCell("Type"),
            new LabelCell("Length"),
            new LabelCell("Precision"),
            new LabelCell("Primary key"),
            new LabelCell("Unique"),
            new LabelCell("Foreign key"),
            new LabelCell("Nullable"),
            new LabelCell("References"),
            new LabelCell("Delete row"),
            new LabelCell("Move row")
    });

    public CreateTablePanel(ITableHandler handler)
    {
        super(new BorderLayout());
        this.handler = handler;
        JPanel propertiesSection = new JPanel(new BorderLayout());
        tableNameField.setBorder(BorderFactory.createTitledBorder("Table name"));
        propertiesSection.setBorder(BorderFactory.createTitledBorder("Properties"));

        JButton addProperty = new JButton("Add property");
        addProperty.setBackground(Color.LIGHT_GRAY);
        addProperty.setAlignmentX(LEFT_ALIGNMENT);
        addProperty.addActionListener(e -> {
            DBTablePropertyPanel propertyPanel = new DBTablePropertyPanel(
                    handler,
                    this::checkNameDuplicated,
                    t -> {
                        SwingUtilities.invokeLater(() -> {
                            propertyTable.getRows().remove(t);
                            propertyTable.getRows().updateUI();
                        });},
                    (t, m) -> {
                        int index;
                        JPanel rows = propertyTable.getRows();
                        int count = rows.getComponentCount();
                        for (index = 0; index < count; index++)
                        {
                            Component c = rows.getComponents()[index];
                            if (c == t)
                            {
                                int next = m ? index - 1 : index + 1;
                                if (next >= 0 && next < count) {
                                    int finalIndex = index;
                                    SwingUtilities.invokeLater(() -> {
                                        rows.remove(finalIndex);
                                        rows.add(c, next);
                                        rows.updateUI();
                                    });
                                }
                                break;
                            }
                        }
                    });
            propertyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, propertyPanel.getPreferredSize().height));
            SwingUtilities.invokeLater(() -> {
                propertyTable.getRows().add(propertyPanel);
                propertyTable.getRows().updateUI();
            });
        });

        JButton createButton = new JButton("Create table");
        createButton.addActionListener(e -> createTable());
        createButton.setBackground(Color.LIGHT_GRAY);

        propertiesSection.add(addProperty, BorderLayout.PAGE_START);
        propertiesSection.add(propertyTable, BorderLayout.CENTER);
        add(tableNameField, BorderLayout.PAGE_START);
        add(propertiesSection, BorderLayout.CENTER);
        add(createButton, BorderLayout.PAGE_END);
    }

    private void createTable()
    {
        if (handler.checkNameExists(tableNameField.getText()))
        {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "The table name has already existed!");
            });
            return;
        }
        List<Property> properties = validateInputs();
        if (properties != null)
        {
            Table table = new Table("SYS", tableNameField.getText(), properties);
            if (handler.createObject(table))
            {
                propertyTable.getRows().clearRows();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Created the table successfully");
                });
            }
            else
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Failed to create the table!"));
        }
    }

    private boolean checkNameDuplicated(String name)
    {
        if (name.contains(" ") || name.contains("."))
            return false;
        for (Component c : propertyTable.getRows().getComponents())
        {
            try
            {
                DBTablePropertyPanel propertyPanel = (DBTablePropertyPanel) c;
                if (propertyPanel.getNameCell().getText().equalsIgnoreCase(name))
                    return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    private java.util.List<Property> validateInputs()
    {
        java.util.List<Property> properties = new ArrayList<>();
        for (Component c : propertyTable.getRows().getComponents())
        {
            try
            {
                DBTablePropertyPanel propertyPanel = (DBTablePropertyPanel) c;
                if (propertyPanel.checkValid())
                {
                    propertyPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
                    properties.add(propertyPanel.getProperty());
                }
                else
                {
                    propertyPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "There are some invalid rows. Please, check your table properties again!"));
                    return null;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return properties;
    }

    //move up down
    //validate
}
