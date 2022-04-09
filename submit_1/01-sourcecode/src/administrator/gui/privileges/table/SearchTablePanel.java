package administrator.gui.privileges.table;

import administrator.dao.permissions.GeneralPermission;
import administrator.dbHandler.table.ITableHandler;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.util.List;

/**
 * administrator.gui.privileges.table
 * Created by NhatLinh - 19127652
 * Date 4/1/2022 - 12:48 AM
 * Description: ...
 */
public class SearchTablePanel extends JPanel {

    private String userName = "";
    private final DefaultComboBoxModel<String> schemaModel = new DefaultComboBoxModel<>();
    private final DefaultComboBoxModel<String> tableNameModel = new DefaultComboBoxModel<>();
    private TablePrivilegePanel privilegePanel = null;
    private final ITableHandler handler;

    public SearchTablePanel(ITableHandler handler)
    {
        super(new BorderLayout());
        this.handler = handler;
        JPanel chooseTablePanel = new JPanel(new GridLayout(3, 1));
        JComboBox<String> schemaCbb = new JComboBox<>(schemaModel);
        schemaCbb.setBorder(BorderFactory.createTitledBorder("Schema"));
        schemaCbb.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                List<String> schemas = handler.getSchemas();
                SwingUtilities.invokeLater(() -> {
                    schemaModel.removeAllElements();
                    schemaModel.addAll(schemas);
                });
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

        schemaCbb.addItemListener(e -> {
            SwingUtilities.invokeLater(tableNameModel::removeAllElements);
            if (e.getItem() != null)
            {
                List<String> tables = handler.getTablesInSchema((String)e.getItem());
                SwingUtilities.invokeLater(() -> {
                    tableNameModel.addAll(tables);
                });
            }
            removePrivilegePanel();
        });



        AutoCompleteDecorator.decorate(schemaCbb);
        JComboBox<String> tableNameCbb = new JComboBox<>(tableNameModel);
        tableNameCbb.addItemListener(e -> {
            removePrivilegePanel();
        });
        tableNameCbb.setBorder(BorderFactory.createTitledBorder("Table"));
        AutoCompleteDecorator.decorate(tableNameCbb);
        JButton getButton = new JButton("View privileges");
        getButton.addActionListener(e -> {
            removePrivilegePanel();
            if (tableNameModel.getSelectedItem() == null || schemaModel.getSelectedItem() == null)
            {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Please select the schema and the table!"));
                return;
            }
            GeneralPermission permission = handler.getPrivileges(userName, (String)schemaModel.getSelectedItem(), (String)tableNameModel.getSelectedItem());
            setPermission(permission);
        });
        chooseTablePanel.add(schemaCbb);
        chooseTablePanel.add(tableNameCbb);
        chooseTablePanel.add(getButton);

        JButton updatePrivilege = new JButton("Update privileges");
        updatePrivilege.addActionListener(e -> {
            if (tableNameModel.getSelectedItem() == null || schemaModel.getSelectedItem() == null || privilegePanel == null)
            {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Please select the schema and the table!"));
                removePrivilegePanel();
                return;
            }
            GeneralPermission permission = privilegePanel.getPermission();
            if (handler.modifyPrivileges((String)schemaModel.getSelectedItem(), (String)tableNameModel.getSelectedItem(), userName, permission))
            {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Modify successfully!"));
                permission = handler.getPrivileges(userName, (String)schemaModel.getSelectedItem(), (String)tableNameModel.getSelectedItem());
                removePrivilegePanel();
                setPermission(permission);
            }
            else
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Failed to modify!"));

        });

        add(chooseTablePanel, BorderLayout.PAGE_START);
        add(Box.createVerticalStrut(100), BorderLayout.CENTER);
        add(updatePrivilege, BorderLayout.PAGE_END);
    }

    public void setUserName(String name) {
        this.userName = name;
        removePrivilegePanel();
    }

    private void removePrivilegePanel()
    {
        final TablePrivilegePanel storedPanel = privilegePanel;
        if (storedPanel != null)
            SwingUtilities.invokeLater(() -> remove(storedPanel));
        privilegePanel = null;
    }

    public void setPermission(GeneralPermission permission)
    {
        privilegePanel = new TablePrivilegePanel(permission);
        SwingUtilities.invokeLater(() -> {
            add(privilegePanel, BorderLayout.CENTER);
            updateUI();
        });
    }
}
