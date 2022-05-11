package users.document.readonly;

import common.gui.table.cells.ComboBoxCell;
import common.gui.table.cells.DatePickerCell;
import common.gui.table.cells.TextCell;
import main.Main;
import main.MainFrame;
import users.dao.DocumentService;

import javax.swing.*;
import java.util.Collection;
import java.util.Date;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

/**
 * users.document.readonly
 * Created by NhatLinh - 19127652
 * Date 5/7/2022 - 3:46 PM
 * Description: ...
 */
public class CreateDocumentServicePanel extends JPanel {

    //String serviceID, Date date, String technicianID, String result
    private final ComboBoxCell serviceIDCbb = new ComboBoxCell();
    private final DatePickerCell date = new DatePickerCell();
    private final ComboBoxCell technicianID = new ComboBoxCell();
    private final TextCell result = new TextCell("");
    private String id;

    public CreateDocumentServicePanel(Supplier<Collection<String>> onGetServices, Supplier<Collection<String>> onGetKtvs,
                                      BiPredicate<String, DocumentService> createDocumentService)
    {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        serviceIDCbb.setDropEvent(onGetServices);
        technicianID.setDropEvent(onGetKtvs);
        JButton confirm = new JButton("Confirm to create the service row");
        confirm.setAlignmentX(CENTER_ALIGNMENT);
        confirm.addActionListener(e -> {
            if (isInputNotValid())
            {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "You need to fill out all the fields"));
            }
            else
            {
                Object[] options = {
                        "Yes, I want to create the row",
                        "No, I don't"};
                int n = JOptionPane.showOptionDialog(this,
                        "Do you really want to create the service row?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (n == 0)
                {
                    if (createDocumentService.test(id, getDocumentService()))
                    {
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(this, "Create the service row successfully"));
                        SwingUtilities.invokeLater(this::reset);
                    }
                    else
                    {
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(this, "Failed to create the service row"));
                    }
                }
            }
        });
        MainFrame.addComponent(this, serviceIDCbb, "Service ID");
        MainFrame.addComponent(this, date.getComponent(), "Date");
        MainFrame.addComponent(this, technicianID, "Technician ID");
        MainFrame.addComponent(this, result, "Result");
        MainFrame.addComponent(this, confirm);
    }

    public void reset()
    {
        serviceIDCbb.setSelectedIndex(-1);
        technicianID.setSelectedIndex(-1);
        result.setText("");
    }


    public void setID(String id)
    {
        this.id = id;
        SwingUtilities.invokeLater(this::reset);
    }

    public DocumentService getDocumentService()
    {
        return new DocumentService(getServiceID(), getDate(), (String) technicianID.getSelectedItem(), result.getText());
    }

    public boolean isInputNotValid()
    {
        return serviceIDCbb.getSelectedIndex() == -1 || date.getDateModel().getValue() == null || technicianID.getSelectedIndex() == -1;
    }

    public String getServiceID()
    {
        return (String) serviceIDCbb.getSelectedItem();
    }

    public Date getDate()
    {
        return date.getDateModel().getValue();
    }
}
