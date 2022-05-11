package users.patient;

import common.gui.table.cells.ICell;
import common.gui.table.cells.LabelCell;
import common.gui.table.row.IRow;
import users.common.RecordListPanel;
import users.dao.DBRecord;
import users.dao.Patient;
import users.dbHandler.DBUserHandler;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * users.patient
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 4:16 PM
 * Description: ...
 */
public class PatientListPanel extends RecordListPanel {

    public PatientListPanel(Supplier<Iterable<DBRecord>> handler, Predicate<Patient> update) {
        super(handler,
                new PatientViewPanel(update),
                new ICell[]{
                    new LabelCell("Patient ID"),
                    new LabelCell("Full name"),
                    new LabelCell("Identity card"),
                    new LabelCell("Day of birth"),
                    new LabelCell("Details")},
                new String[] {"Patient ID", "Identity card"});
    }

    @Override
    protected IRow getRow(DBRecord record, Runnable detailCallback) {
        return new PatientRow((Patient)record, detailCallback);
    }

    @Override
    protected void hint() {

    }
}
