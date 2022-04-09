package administrator.gui.privileges.table;

import administrator.dao.permissions.ActionPermission;
import administrator.dao.permissions.DetailedActionPermission;
import administrator.dao.permissions.PermissionType;
import administrator.gui.table.cells.CheckBoxCell;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * administrator.gui.privileges.table
 * Created by NhatLinh - 19127652
 * Date 4/2/2022 - 3:10 PM
 * Description: ...
 */
public class TableActionOnColumnPanel extends TableActionPrivilegePanel {

    private final List<CheckBoxCell> columnChecks = new ArrayList<>();

    public TableActionOnColumnPanel(PermissionType action, DetailedActionPermission actionPermission) {
        super(action, actionPermission);
    }

    @Override
    protected void resetGranted()
    {
        super.resetGranted();
        for (CheckBoxCell c : columnChecks)
            c.setSelected(false);
    }

    @Override
    public void addColumnComponents(int count)
    {
        DetailedActionPermission permission = (DetailedActionPermission)currentPermission;
        for (int i = 0; i < count; i++)
        {
            CheckBoxCell boxCell = new CheckBoxCell(permission.getColumns().get(i));
            final int finalI = i;
            boxCell.addActionListener(e -> {
                checkEditMode(permission.getColumns().get(finalI), boxCell);
                checkNotGranted(boxCell);
            });
            columnChecks.add(boxCell);
            addComponents.add(boxCell);
        }
    }


    @Override
    protected void reset()
    {
        super.reset();
        List<Boolean> columnPrivileges = ((DetailedActionPermission)currentPermission).getColumns();
        for (int i = 0; i < columnPrivileges.size(); i++)
        {
            final int finalI = i;
            SwingUtilities.invokeLater(() -> {
                columnChecks.get(finalI).setSelected(columnPrivileges.get(finalI));
            });
        }
    }

    @Override
    public ActionPermission getPermissions ()
    {
        return new DetailedActionPermission(grantedCb.isSelected(), wgoCb.isSelected(), columnChecks.stream().map(AbstractButton::isSelected).toList());
    }
}
