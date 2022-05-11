package users.staff;

import common.gui.table.cells.ICell;
import common.gui.table.cells.LabelCell;
import common.gui.table.row.IRow;
import users.common.RecordListPanel;
import users.dao.DBRecord;
import users.dao.Staff;
import users.dbHandler.DBUserHandler;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * users.staff
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 4:51 PM
 * Description: ...
 */
public class StaffListPanel extends RecordListPanel {

    public StaffListPanel(Supplier<Iterable<DBRecord>> handler, Predicate<Staff> update) {
        super(handler,
                new StaffViewPanel(update),
                new ICell[]{
                        new LabelCell("Staff ID"),
                        new LabelCell("Full name"),
                        new LabelCell("Sex"),
                        new LabelCell("Day of birth"),
                        new LabelCell("Role"),
                        new LabelCell("Details")},
                new String[] {"Staff ID"});
    }

    @Override
    protected IRow getRow(DBRecord record, Runnable detailCallback) {
        return new StaffRow((Staff) record, detailCallback);
    }

    @Override
    protected void hint() {

    }
}
