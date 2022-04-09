package administrator.gui.privileges.table;

import administrator.dao.permissions.ActionPermission;
import administrator.dao.permissions.DetailedActionPermission;
import administrator.dao.permissions.GeneralPermission;
import administrator.dao.permissions.PermissionType;
import administrator.gui.table.cells.LabelCell;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * administrator.gui.privileges.table
 * Created by NhatLinh - 19127652
 * Date 4/1/2022 - 12:57 AM
 * Description: ...
 */
public class TablePrivilegePanel extends JPanel {

    private final List<TableActionPrivilegePanel> privilegePanels;
    private final  GeneralPermission generalPermission;

    public TablePrivilegePanel(GeneralPermission permission) {
        super(new GridLayout(0, 5));
        this.generalPermission = permission;
        int countCols = permission.properties().size();
        Map<PermissionType, ActionPermission> permissionMap = permission.permissions();
        privilegePanels = new ArrayList<>() {
            {
                add(new TableActionOnColumnPanel(PermissionType.SELECT, (DetailedActionPermission) permissionMap.get(PermissionType.SELECT)));
                add(new TableActionOnColumnPanel(PermissionType.UPDATE, (DetailedActionPermission) permissionMap.get(PermissionType.UPDATE)));
                add(new TableActionPrivilegePanel(PermissionType.INSERT, permissionMap.get(PermissionType.INSERT)));
                add(new TableActionPrivilegePanel(PermissionType.DELETE, permissionMap.get(PermissionType.DELETE)));
            }
        };
        for (TableActionPrivilegePanel p : privilegePanels)
            p.addColumnComponents(countCols);

        add(new LabelCell("PRIVILEGE/ACTION"));
        for (TableActionPrivilegePanel p : privilegePanels)
            add(p.actionLab);

        add(new LabelCell(("MODIFY")));
        for (TableActionPrivilegePanel p : privilegePanels)
            add(p.modifyCb);

        add(new LabelCell(("GRANTED")));
        for (TableActionPrivilegePanel p : privilegePanels)
            add(p.grantedCb);

        add(new LabelCell(("WITH GRANT OPTION")));
        for (TableActionPrivilegePanel p : privilegePanels)
            add(p.wgoCb);

        for (int i = 0; i < countCols; i++)
        {
            add(new LabelCell(permission.properties().get(i)));
            for (TableActionPrivilegePanel p : privilegePanels)
                add(p.addComponents.get(i));
        }
    }

    public GeneralPermission getPermission()
    {
        Map<PermissionType, ActionPermission> permissionMap = new HashMap<>();
        for (TableActionPrivilegePanel t : privilegePanels)
            if (t.modifyCb.isSelected())
                permissionMap.put(PermissionType.valueOf(t.actionLab.getText()), t.getPermissions());
        return new GeneralPermission(generalPermission.properties(), permissionMap);
    }
}
