package users.staff;

import common.gui.table.cells.ICell;
import common.gui.table.cells.LabelCell;
import common.gui.table.row.NColumnsPanel;
import users.dao.Staff;

import java.awt.*;

/**
 * users.staff
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 4:51 PM
 * Description: ...
 */
public class StaffRow extends NColumnsPanel {
    private final String id;
    public StaffRow(Staff staff, Runnable callback)
    {
        super(new ICell[]{
                new LabelCell(staff.id()),
                new LabelCell(staff.name()),
                new LabelCell(staff.sex().toString()),
                new LabelCell(staff.dob().toString()),
                new LabelCell(staff.role()),
                new LabelCell("Details", callback)
        });
        id = staff.id();
    }

    @Override
    public String getHeader() {
        return id;
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
