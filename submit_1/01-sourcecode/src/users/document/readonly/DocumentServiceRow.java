package users.document.readonly;

import common.gui.table.cells.*;
import common.gui.table.row.NColumnsPanel;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import users.dao.DocumentService;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * users.document.service
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 6:02 PM
 * Description: ...
 */
public class DocumentServiceRow extends NColumnsPanel {

    private final String serviceID;
    private final Date date;

    public DocumentServiceRow(DocumentService documentService, Runnable onDel)
    {
        super(new ICell[]{
                new LabelCell(documentService.serviceID()),
                new LabelCell(documentService.date().toString()),
                new LabelCell(documentService.ktvID()),
                new LabelCell(documentService.result()),
                new ButtonCell("Delete", onDel)
        });
        serviceID = documentService.serviceID();
        date = documentService.date();
    }

    @Override
    public String getHeader() {
        return serviceID + date.toString();
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
