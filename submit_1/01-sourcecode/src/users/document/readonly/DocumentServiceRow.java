package users.document.readonly;

import common.gui.table.cells.*;
import common.gui.table.row.NColumnsPanel;
import users.dao.DocumentService;

import java.awt.*;
import java.util.Date;

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
                new LabelCell(documentService.technicianID()),
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
