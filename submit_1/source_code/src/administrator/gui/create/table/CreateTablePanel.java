package administrator.gui.create.table;

import administrator.dao.table.Table;
import administrator.dao.table.property.Property;
import administrator.dbHandler.IDBAHandler;
import administrator.dbHandler.table.ITableHandler;
import common.gui.table.ITablePanel;
import common.gui.table.TablePanel;
import common.gui.table.cells.ICell;
import common.gui.table.cells.LabelCell;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
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
    private final JComboBox<String> schemaField = new JComboBox<>(new DefaultComboBoxModel<>());
    private final ITablePanel propertyTable = new TablePanel(new ICell[]{
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
        AutoCompleteDecorator.decorate(schemaField);
        schemaField.setBorder(BorderFactory.createTitledBorder("Schema"));
        schemaField.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                List<String> schemas = handler.getSchemas();
                DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>)schemaField.getModel();
                SwingUtilities.invokeLater(() -> {
                    model.removeAllElements();
                    if (schemas != null && !schemas.isEmpty())
                        model.addAll(schemas);
                });
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        tableNameField.setBorder(BorderFactory.createTitledBorder("Table name"));
        JPanel nameSection = new JPanel(new GridLayout(2, 1));
        nameSection.add(schemaField);
        nameSection.add(tableNameField);

        propertiesSection.setBorder(BorderFactory.createTitledBorder("Properties"));

        JButton addProperty = new JButton("Add property");
        addProperty.setBackground(Color.LIGHT_GRAY);
        addProperty.setAlignmentX(LEFT_ALIGNMENT);
        addProperty.addActionListener(e -> {
            DBTablePropertyPanel propertyPanel = new DBTablePropertyPanel(
                    handler,
                    this::checkNameDuplicated,
                    t -> {
                        propertyTable.remove(t);
                        SwingUtilities.invokeLater(propertyTable::updateUI);},
                    (t, m) -> {
                        int index;
                        Component[] cs = propertyTable.getComponents();
                        int count = cs.length;
                        for (index = 0; index < count; index++)
                        {
                            Component c = cs[index];
                            if (c == t)
                            {
                                int next = m ? index - 1 : index + 1;
                                if (next >= 0 && next < count) {
                                    propertyTable.swapRow(index, next);
                                    SwingUtilities.invokeLater(propertyTable::updateUI);
                                }
                                break;
                            }
                        }
                    },
                    schemaField::getSelectedItem);
            //propertyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, propertyPanel.getPreferredSize().height));
            propertyTable.addRow(propertyPanel);
            SwingUtilities.invokeLater(propertyTable::updateUI);
        });

        JButton createButton = new JButton("Create table");
        createButton.addActionListener(e -> createTable());
        createButton.setBackground(Color.LIGHT_GRAY);

        propertiesSection.add(addProperty, BorderLayout.PAGE_START);
        propertiesSection.add(propertyTable.getAddComponent(), BorderLayout.CENTER);
        add(nameSection, BorderLayout.PAGE_START);
        add(propertiesSection, BorderLayout.CENTER);
        add(createButton, BorderLayout.PAGE_END);
    }

    private void createTable()
    {
        Object schema = schemaField.getSelectedItem();
        String tableName = tableNameField.getText();
        if (schema == null)
        {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "The schema has not been chosen!"));
            return;
        }
        if (tableName == null || tableName.isBlank() || tableName.contains(" "))
        {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "The tableName name is not valid!"));
            return;
        }
        if (handler.checkNameExists(schema + "."  + tableName))
        {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "The tableName name has already existed!"));
            return;
        }

        List<Property> properties = validateInputs();
        if (properties != null && !properties.isEmpty())
        {
            Table table = new Table((String)schema, tableName, properties);
            if (handler.createObject(table))
            {
                propertyTable.clearRows();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Created the tableName successfully"));
            }
            else
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Failed to create the tableName!"));
        }
        else
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "There is no property!"));
    }

    private boolean checkNameDuplicated(String name)
    {
        if (name.contains(" ") || name.contains("."))
            return false;
        int count = 0;
        for (Component c : propertyTable.getComponents())
        {
            try
            {
                DBTablePropertyPanel propertyPanel = (DBTablePropertyPanel) c;
                if (propertyPanel.getNameCell().getText().equalsIgnoreCase(name) && ++count == 2)
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
        for (Component c : propertyTable.getComponents())
        {
            try
            {
                DBTablePropertyPanel propertyPanel = (DBTablePropertyPanel) c;
                if (propertyPanel.checkValid())
                {
                    SwingUtilities.invokeLater(() -> propertyPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN)));
                    properties.add(propertyPanel.getProperty());
                }
                else
                {
                    SwingUtilities.invokeLater(() -> propertyPanel.setBorder(BorderFactory.createLineBorder(Color.RED)));
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
