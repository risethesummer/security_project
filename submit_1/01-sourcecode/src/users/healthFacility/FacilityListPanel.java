package users.healthFacility;

import common.gui.table.cells.ICell;
import common.gui.table.cells.LabelCell;
import common.gui.table.row.IRow;
import common.gui.table.row.NColumnsPanel;
import users.common.RecordListPanel;
import users.dao.DBRecord;
import users.dao.Facility;
import users.dbHandler.DBUserHandler;

import java.awt.*;
import java.util.function.Supplier;

/**
 * users.staff
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 4:51 PM
 * Description: ...
 */
public class FacilityListPanel extends RecordListPanel {

    public FacilityListPanel(Supplier<Iterable<DBRecord>> handler) {
        super(handler,
                null,
                new ICell[]{
                        new LabelCell("Facility ID"),
                        new LabelCell("Facility name"),
                        new LabelCell("Facility address"),
                        new LabelCell("Facility hotline")},
                new String[] {"Facility ID"});
    }

    @Override
    protected IRow getRow(DBRecord record, Runnable detailCallback) {
        Facility facility = (Facility) record;
        return new NColumnsPanel(new ICell[]{
                new LabelCell(facility.id()),
                new LabelCell(facility.name()),
                new LabelCell(facility.address()),
                new LabelCell(facility.phone())
        }) {
            @Override
            public String getHeader() {
                return facility.id();
            }

            @Override
            public Component getComponent() {
                return this;
            }
        };
    }

    @Override
    protected void hint() {

    }
}
