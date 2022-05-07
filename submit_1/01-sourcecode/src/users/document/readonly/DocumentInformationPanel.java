package users.document.readonly;
import common.gui.table.ITablePanel;
import common.gui.table.TablePanel;
import common.gui.table.cells.ICell;
import common.gui.table.cells.LabelCell;
import common.gui.table.row.IRow;
import main.MainFrame;
import users.dao.Document;
import users.dao.DocumentService;

import javax.swing.*;

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
            new LabelCell("KTV ID"),
            new LabelCell("Result"),
            new LabelCell("Delete")
    });

    public DocumentInformationPanel()
    {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        MainFrame.addComponentAndDisable(this, id, "Document ID");
        MainFrame.addComponentAndDisable(this, patientID, "Patient ID");
        MainFrame.addComponentAndDisable(this, date, "Date");
        MainFrame.addComponentAndDisable(this, diagnose, "Diagnose");
        MainFrame.addComponentAndDisable(this, doctorID, "Doctor ID");
        MainFrame.addComponentAndDisable(this, departmentID, "Department ID");
        MainFrame.addComponentAndDisable(this, facilityID, "Facility ID");
        MainFrame.addComponentAndDisable(this, conclusion, "Conclusion");
        MainFrame.addComponent(this, services.getAddComponent(), "Services");
    }

    public void setDocument(Document document)
    {
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
            services.deleteRow(service.serviceID() + service.date().toString());
        });
    }
}
