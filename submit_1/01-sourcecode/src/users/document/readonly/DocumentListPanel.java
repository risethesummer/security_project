package users.document.readonly;

import common.gui.table.cells.ButtonCell;
import common.gui.table.cells.ICell;
import common.gui.table.cells.LabelCell;
import common.gui.table.row.IRow;
import users.common.RecordListPanel;
import users.dao.DBRecord;
import users.dao.Document;

import java.util.function.Supplier;

/**
 * users.patient
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 4:16 PM
 * Description: ...
 */
public class DocumentListPanel extends RecordListPanel {

    public DocumentListPanel(Supplier<Iterable<DBRecord>> handler) {
        super(handler,
                new DocumentViewPanel(),
                new ICell[]{
                    new LabelCell("Document ID"),
                    new LabelCell("Patient ID"),
                    new LabelCell("Date"),
                    new LabelCell("Diagnose"),
                    new LabelCell("Conclusion"),
                    new ButtonCell("Details")
                },
                new String[] {"Document ID", "Patient ID"});
    }

    @Override
    protected IRow getRow(DBRecord record, Runnable detailCallback) {
        return new DocumentRow((Document) record, detailCallback);
    }

    @Override
    protected void hint() {

    }
}
