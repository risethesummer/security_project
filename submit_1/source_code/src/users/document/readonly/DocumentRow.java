package users.document.readonly;

import common.gui.table.cells.ButtonCell;
import common.gui.table.cells.ICell;
import common.gui.table.cells.LabelCell;
import common.gui.table.row.NColumnsPanel;
import users.dao.Document;
import users.dao.Patient;

import java.awt.*;

/**
 * users.patient
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 2:10 PM
 * Description: ...
 */
public class DocumentRow extends NColumnsPanel {
    private final String id;
    public DocumentRow(Document document, Runnable callback)
    {
        super(new ICell[]{
                new LabelCell(document.id()),
                new LabelCell(document.patientID()),
                new LabelCell(document.date().toString()),
                new LabelCell(document.diagnose()),
                new LabelCell(document.conclusion()),
                new ButtonCell("Details", callback)
        });
        id = document.id();
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
