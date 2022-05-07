package users.common;

import common.gui.table.ITablePanel;
import common.gui.table.TablePanel;
import common.gui.table.cells.ICell;
import common.gui.table.row.IRow;
import common.gui.SearchPanel;
import users.dao.DBRecord;
import users.dbHandler.DBUserHandler;

import java.util.function.Supplier;

/**
 * users.patient
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 2:17 PM
 * Description: ...
 */
public abstract class RecordListPanel extends SearchPanel {
    protected final RecordViewPanel recordView;
    protected final ITablePanel listPanel;
    protected final Supplier<Iterable<DBRecord>> getRecords;

    public RecordListPanel(Supplier<Iterable<DBRecord>> getRecords, RecordViewPanel recordView, ICell[] listTitles, String[] searchTitles) {
        super(searchTitles);
        this.getRecords = getRecords;
        this.recordView = recordView;
        this.listPanel = new TablePanel(listTitles);
        this.recordView.setBackCallback(() -> setMainSection(this.listPanel.getAddComponent()));
        setMainSection(this.listPanel.getAddComponent());
    }

    @Override
    protected void reload()
    {
        Iterable<DBRecord> records = getRecords.get();
        listPanel.clearRows();
        for (DBRecord r : records)
        {
            IRow row = getRow(r, () -> {
                recordView.setRecord(r);
                setMainSection(recordView);
            });
            listPanel.addRow(row);
        }
    }

    protected abstract IRow getRow(DBRecord DBRecord, Runnable detailCallback);
    protected abstract void hint();
}
