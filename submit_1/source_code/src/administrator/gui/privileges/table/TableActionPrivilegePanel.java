package administrator.gui.privileges.table;

import administrator.dao.permissions.ActionPermission;
import administrator.dao.permissions.PermissionType;
import common.gui.table.cells.CheckBoxCell;
import common.gui.table.cells.LabelCell;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * administrator.gui.privileges.table
 * Created by NhatLinh - 19127652
 * Date 4/1/2022 - 1:12 AM
 * Description: ...
 */
public class TableActionPrivilegePanel {

    public final List<Component> addComponents = new ArrayList<>();
    public final LabelCell actionLab;
    public final CheckBoxCell modifyCb = new CheckBoxCell(false);
    public final CheckBoxCell grantedCb;
    public final CheckBoxCell wgoCb;
    public final ActionPermission currentPermission;
    private static final int shouldHave = 4;

    public TableActionPrivilegePanel(PermissionType action, ActionPermission actionPermission) {
        this.currentPermission = actionPermission;
        this.actionLab = new LabelCell(action.toString());
        this.wgoCb = new CheckBoxCell(actionPermission.isWgo());
        this.grantedCb = new CheckBoxCell(actionPermission.isGranted());
        grantedCb.addActionListener(e -> {
            if (checkEditMode(currentPermission.isGranted(), grantedCb))
                return;
            if (!grantedCb.isSelected())
                resetGranted();
        });
        wgoCb.addItemListener(e -> {
            if (checkEditMode(currentPermission.isWgo(), wgoCb))
                return;
            checkNotGranted(wgoCb);
        });
        modifyCb.addItemListener(e -> {
            if (!modifyCb.isSelected())
                reset();
        });
    }

    protected void resetGranted()
    {
        wgoCb.setSelected(false);
    }

    protected void reset()
    {
        SwingUtilities.invokeLater(() -> {
            grantedCb.setSelected(currentPermission.isGranted());
            wgoCb.setSelected(currentPermission.isWgo());
        });
    }


    public void addColumnComponents(int count)
    {
        for (int i = 0; i < count; i++)
            addComponents.add(Box.createRigidArea(new Dimension(20, 20)));
            //addComponents.add(new JTextField("None"));
    }

    public boolean checkNotGranted(JCheckBox checkBox)
    {
        if (checkBox.isSelected() && !grantedCb.isSelected()) {
            SwingUtilities.invokeLater(() -> {
                checkBox.setSelected(false);
                JOptionPane.showMessageDialog(checkBox, "You are not granted to do this!");
            });
            return true;
        }
        return false;
    }

    public boolean checkEditMode(boolean original, JCheckBox checkBox)
    {
        if (checkBox.isSelected() != original && !modifyCb.isSelected())
        {
            SwingUtilities.invokeLater(() -> {
                checkBox.setSelected(original);
                JOptionPane.showMessageDialog(checkBox, "You are not in edit mode!");
            });
            return true;
        }
        return false;
    }

    public ActionPermission getPermissions ()
    {
        return new ActionPermission(grantedCb.isSelected(), wgoCb.isSelected());
    }
}
