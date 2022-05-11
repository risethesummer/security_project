package users.notification;

import common.gui.table.cells.ICell;
import common.gui.table.cells.LabelCell;
import common.gui.table.row.IRow;
import common.gui.table.row.NColumnsPanel;
import users.common.RecordListPanel;
import users.common.RecordViewPanel;
import users.dao.DBRecord;
import users.dao.Notification;

import java.awt.*;
import java.util.function.Supplier;

/**
 * users.notification
 * Created by NhatLinh - 19127652
 * Date 5/8/2022 - 11:17 AM
 * Description: ...
 */
public class NotificationListPanel extends RecordListPanel {


    public NotificationListPanel(Supplier<Iterable<DBRecord>> getRecords)
    {
        super(getRecords, null, new ICell[]{
                new LabelCell("Content"),
                new LabelCell("Date time"),
                new LabelCell("Location")
        }, new String[]{"Content"});
    }

    @Override
    protected IRow getRow(DBRecord record, Runnable detailCallback) {

        Notification notification = (Notification) record;

        return new NColumnsPanel(new ICell[]{
                new LabelCell(notification.content()),
                new LabelCell(notification.dateTime().toString()),
                new LabelCell(notification.location())
        }) {
            @Override
            public String getHeader() {
                return notification.content();
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
