package users.patient;

import common.gui.table.cells.ICell;
import common.gui.table.cells.LabelCell;
import common.gui.table.row.NColumnsPanel;
import users.dao.Patient;

import java.awt.*;

/**
 * users.patient
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 2:10 PM
 * Description: ...
 */
public class PatientRow extends NColumnsPanel {
    private final String id;
    public PatientRow(Patient patient, Runnable callback)
    {
        super(new ICell[]{
                new LabelCell(patient.id()),
                new LabelCell(patient.name()),
                new LabelCell(patient.idCard()),
                new LabelCell(patient.dob().toString()),
                new LabelCell("Details", callback)
        });
        id = patient.id();
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
