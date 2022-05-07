package users;

import common.gui.DisposableFrame;
import users.dbHandler.DBUserHandler;
import users.document.readonly.DocumentListPanel;
import users.document.updatable.NewDocumentInformationPanel;
import users.healthFacility.FacilityListPanel;
import users.patient.PatientListPanel;
import users.staff.StaffListPanel;

import javax.swing.*;

/**
 * users
 * Created by NhatLinh - 19127652
 * Date 5/7/2022 - 3:17 PM
 * Description: ...
 */
public class UserFrame extends DisposableFrame {

    public UserFrame(Runnable onClose, DBUserHandler handler)
    {
        super(onClose);
        JTabbedPane documentTab = new JTabbedPane(JTabbedPane.TOP);
        documentTab.add("View documents", new DocumentListPanel(handler::getDocuments));
        documentTab.add("Create new document", new NewDocumentInformationPanel(handler::createDocument,
                handler::getServiceIDs, handler::getStaffIDs));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.add("Patients", new PatientListPanel(handler::getPatients));
        tabbedPane.add("Staffs", new StaffListPanel(handler::getStaffs));
        tabbedPane.add("Documents", documentTab);
        tabbedPane.add("Facilities", new FacilityListPanel(handler::getFacilities));
        getContentPane().add(tabbedPane);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        //pack();
    }


}
