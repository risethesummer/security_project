package users.document.readonly;
import common.gui.table.ITablePanel;
import common.gui.table.TablePanel;
import common.gui.table.cells.ICell;
import common.gui.table.cells.LabelCell;
import common.gui.table.row.IRow;
import main.Main;
import main.MainFrame;
import users.dao.Document;
import users.dao.DocumentService;

import javax.swing.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * users.patient
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 1:12 PM
 * Description: ...
 */
public class DocumentInformationPanel extends JPanel {

    private final JTextField id = new JTextField("");
    private final JTextField patientID = new JTextField("");
    private final JTextField date = new JTextField("");
    private final JTextField diagnose = new JTextField("");
    private final JTextField doctorID = new JTextField("");
    private final JTextField departmentID = new JTextField("");
    private final JTextField facilityID = new JTextField("");
    private final JTextField conclusion = new JTextField("");
    private final ITablePanel services = new TablePanel(new ICell[]{
            new LabelCell("Service ID"),
            new LabelCell("Date"),
            new LabelCell("Technician ID"),
            new LabelCell("Result"),
            new LabelCell("Delete")
    });
    private final CreateDocumentServicePanel createDocumentServicePanel;
    private final JButton delBtn = new JButton("Delete the document");
    private final BiPredicate<String, DocumentService> onDelService;

    public DocumentInformationPanel(CreateDocumentServicePanel createDocumentServicePanel,
                                    Predicate<String> onDelDocument,
                                    BiPredicate<String, DocumentService> onDelService)
    {
        super();
        this.onDelService = onDelService;
        this.createDocumentServicePanel = createDocumentServicePanel;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        delBtn.setAlignmentX(CENTER_ALIGNMENT);
        delBtn.addActionListener(e -> {
            Object[] options = {
                    "Yes, I want to delete the document",
                    "No, I don't"};
            int n = JOptionPane.showOptionDialog(this,
                    "Do you really want to delete the document?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (n == 0)
            {
                if (onDelDocument.test(id.getText()))
                {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Delete the document successfully"));
                }
                else
                {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Failed to delete the document"));
                }
            }
        });
        MainFrame.addComponent(this, delBtn);
        MainFrame.addComponentAndDisable(this, id, "Document ID");
        MainFrame.addComponentAndDisable(this, patientID, "Patient ID");
        MainFrame.addComponentAndDisable(this, date, "Date");
        MainFrame.addComponentAndDisable(this, diagnose, "Diagnose");
        MainFrame.addComponentAndDisable(this, doctorID, "Doctor ID");
        MainFrame.addComponentAndDisable(this, departmentID, "Department ID");
        MainFrame.addComponentAndDisable(this, facilityID, "Facility ID");
        MainFrame.addComponentAndDisable(this, conclusion, "Conclusion");
        add(services.getAddComponent());
        MainFrame.addComponent(this, createDocumentServicePanel, "Add new service row");
    }

    public void setDocument(Document document)
    {
        createDocumentServicePanel.setID(document.id());
        id.setText(document.id());
        patientID.setText(document.patientID());
        date.setText(document.date().toString());
        diagnose.setText(document.diagnose());
        doctorID.setText(document.dortorID());
        departmentID.setText(document.departmentID());
        facilityID.setText(document.facilityID());
        conclusion.setText(document.conclusion());
        services.clearRows();
        for (DocumentService service : document.services())
            services.addRow(getServiceRow(service));
    }

    public IRow getServiceRow(DocumentService service)
    {
        return new DocumentServiceRow(service, () -> {
            if (onDelService.test(id.getText(), service))
            {
                services.deleteRow(service.serviceID() + service.date().toString());
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Delete the service row successfully"));
            }
            else
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Failed to delete the service row"));
        });
    }
}
