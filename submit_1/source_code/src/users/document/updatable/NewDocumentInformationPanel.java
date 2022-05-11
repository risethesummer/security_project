package users.document.updatable;

import common.gui.table.ITablePanel;
import common.gui.table.TablePanel;
import common.gui.table.cells.ComboBoxCell;
import common.gui.table.cells.ICell;
import common.gui.table.cells.LabelCell;
import main.MainFrame;
import users.dao.Document;
import users.dao.DocumentService;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * users.document
 * Created by NhatLinh - 19127652
 * Date 5/7/2022 - 12:47 PM
 * Description: ...
 */
public class NewDocumentInformationPanel extends JPanel {

    private final JTextField id = new JTextField("");
    private final ComboBoxCell patientID = new ComboBoxCell();
    private final JTextField diagnose = new JTextField("");
    private final ComboBoxCell doctorID = new ComboBoxCell();
    private final ComboBoxCell departmentID = new ComboBoxCell();
    private final ComboBoxCell facilityID = new ComboBoxCell();
    private final JTextField conclusion = new JTextField("");
 /*   private final ITablePanel services = new TablePanel(new ICell[]{
            new LabelCell("Service ID"),
            new LabelCell("Date"),
            new LabelCell("KTV ID"),
            new LabelCell("Result"),
            new LabelCell("Delete")
    });
*/
    public NewDocumentInformationPanel(Predicate<Document> createDocument,
                                       Supplier<Collection<String>> onGetPatients,
                                       Supplier<Collection<String>> onGetDoctors,
                                       Supplier<Collection<String>> onGetDepartments,
                                       Supplier<Collection<String>> onGetFacilities)
    {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        MainFrame.addComponent(this, id, "Document ID");
        MainFrame.addComponent(this, patientID, "Patient ID");
        //MainFrame.addComponentAndDisable(this, date, "Date");
        MainFrame.addComponent(this, diagnose, "Diagnose");
        MainFrame.addComponent(this, doctorID, "Doctor ID");
        MainFrame.addComponent(this, departmentID, "Department ID");
        MainFrame.addComponent(this, facilityID, "Facility ID");
        MainFrame.addComponent(this, conclusion, "Conclusion");
        patientID.setDropEvent(onGetPatients);
        doctorID.setDropEvent(onGetDoctors);
        departmentID.setDropEvent(onGetDepartments);
        facilityID.setDropEvent(onGetFacilities);
        //JButton addServiceBtn = new JButton("Add service");
      /*  addServiceBtn.addActionListener(e -> {
            NewDocumentServiceRow row = new NewDocumentServiceRow(
                    onGetServices,
                    onGetKTVs,
                    services::remove
            );
            SwingUtilities.invokeLater(() -> services.addRow(row));
        });
        MainFrame.addComponent(this, addServiceBtn);
        add(services.getAddComponent());*/
        JButton createBtn = new JButton("Create the document");
        createBtn.setAlignmentX(CENTER_ALIGNMENT);
        createBtn.addActionListener(e -> {
            if (!isInputNotValid())
            {
                Object[] options = {
                        "Yes, I want to create the document",
                        "No, I don't"};
                int n = JOptionPane.showOptionDialog(this,
                        "Do you really want to create the document?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (n == 0)
                {
                    Document document = getDocument();
                    if (createDocument.test(document))
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Create the document successfully"));
                    else
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Failed to create the document"));
                }
            }
            else
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "You need to fill out all the fields"));
        });
        MainFrame.addComponent(this, createBtn);
    }

    public boolean isInputNotValid()
    {
        return id.getText().isBlank() ||
                        patientID.getSelectedIndex() == -1 ||
                        diagnose.getText().isBlank() ||
                        doctorID.getSelectedIndex() == -1 ||
                        departmentID.getSelectedIndex() == -1 ||
                        facilityID.getSelectedIndex() == -1;
       /* if (!check)
        {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "You have to fulfil all the fields to create a new document!"));
            return true;
        }
        for (Component c : services.getComponents())
        {
            try
            {
                NewDocumentServiceRow row = (NewDocumentServiceRow)c;
                if (row.isInputNotValid())
                {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "You have to fulfil all the service rows!"));
                    return true;
                }
            }
            catch (Exception e)
            {
            }
        }*/

        //return false;
    }

    public Document getDocument()
    {
/*        List<DocumentService> inputServices = new ArrayList<>();
        for (Component c : services.getComponents())
        {
            try
            {
                NewDocumentServiceRow row = (NewDocumentServiceRow)c;
                if (row.isInputNotValid())
                    inputServices.add(row.getDocumentService());
            }
            catch (Exception e)
            {
            }
        }*/
        return new Document(id.getText(),
                (String)patientID.getSelectedItem(),
                Date.from(Instant.now()),
                diagnose.getText(),
                (String)doctorID.getSelectedItem(),
                (String)departmentID.getSelectedItem(),
                (String)facilityID.getSelectedItem(),
                conclusion.getText(),
                null);
    }
}
