package users.document.readonly;

import common.gui.table.cells.ButtonCell;
import common.gui.table.cells.ComboBoxCell;
import common.gui.table.cells.DatePickerCell;
import common.gui.table.cells.TextCell;
import main.MainFrame;
import users.dao.DocumentService;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * users.document.readonly
 * Created by NhatLinh - 19127652
 * Date 5/7/2022 - 3:46 PM
 * Description: ...
 */
public class CreateDocumentServicePanel extends JPanel {

    //String serviceID, Date date, String ktvID, String result
    private final ComboBoxCell serviceIDCbb = new ComboBoxCell();
    private final DatePickerCell date = new DatePickerCell();
    private final ComboBoxCell ktvID = new ComboBoxCell();
    private final TextCell result = new TextCell("");
    private final JButton confirm = new JButton("Confirm to create the service row");

    public CreateDocumentServicePanel(Supplier<Collection<String>> onGetServices, Supplier<Collection<String>> onGetKtvs)
    {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        serviceIDCbb.setDropEvent(onGetServices);
        ktvID.setDropEvent(onGetKtvs);
        confirm.addActionListener(e -> {

        });
        MainFrame.addComponent(this, serviceIDCbb, "Service ID");
        MainFrame.addComponent(this, date, "Date");
        MainFrame.addComponent(this, ktvID, "KTV ID");
        MainFrame.addComponent(this, result, "Service ID");
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
