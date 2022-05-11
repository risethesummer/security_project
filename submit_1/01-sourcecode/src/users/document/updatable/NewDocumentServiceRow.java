package users.document.updatable;

import common.gui.table.cells.*;
import common.gui.table.row.NColumnsPanel;
import users.dao.DocumentService;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.util.Collection;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * users.document.service
 * Created by NhatLinh - 19127652
 * Date 5/7/2022 - 12:00 PM
 * Description: ...
 */
public class NewDocumentServiceRow extends NColumnsPanel {

    //String serviceID, Date date, String technicianID, String result
    private final ComboBoxCell serviceIDCbb = new ComboBoxCell();
    private final DatePickerCell date = new DatePickerCell();
    private final ComboBoxCell ktvID = new ComboBoxCell();
    private final TextCell result = new TextCell("");


    public NewDocumentServiceRow(Supplier<Collection<String>> onGetServices, Supplier<Collection<String>> onGetKtvs,
                              Consumer<JComponent> onDelete)
    {
        super(5);
        serviceIDCbb.setDropEvent(onGetServices);
        ktvID.setDropEvent(onGetKtvs);
        addCell(serviceIDCbb, date, ktvID, result, new ButtonCell("Delete", () -> onDelete.accept(this)));
    }

    public DocumentService getDocumentService()
    {
        return new DocumentService(getServiceID(), getDate(), (String)ktvID.getSelectedItem(), result.getText());
    }

    public boolean isInputNotValid()
    {
        return serviceIDCbb.getSelectedIndex() == -1 || date.getDateModel().getValue() == null || ktvID.getSelectedIndex() == -1;
    }

    public String getServiceID()
    {
        return (String) serviceIDCbb.getSelectedItem();
    }

    public Date getDate()
    {
        return date.getDateModel().getValue();
    }

    @Override
    public String getHeader() {
        return (String) serviceIDCbb.getSelectedItem();
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
