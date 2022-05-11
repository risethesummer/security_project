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
        if (this.recordView != null)
            this.recordView.setBackCallback(() -> addOrigin(this.listPanel.getAddComponent()));
        addOrigin(this.listPanel.getAddComponent());
        reload();
    }

    @Override
    protected void reload()
    {
        Iterable<DBRecord> records = getRecords.get();
        listPanel.clearRows();
        if (records != null)
        {
            for (DBRecord r : records)
            {
                IRow row = getRow(r, () -> {
                    if (recordView != null)
                    {
                        recordView.setRecord(r);
                        setMainSection(recordView);
                    }
                });
                listPanel.addRow(row);
            }
        }
        listPanel.updateUI();
    }

    protected abstract IRow getRow(DBRecord DBRecord, Runnable detailCallback);
    protected abstract void hint();
}
