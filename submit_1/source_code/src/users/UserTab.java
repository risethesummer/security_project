package users;

import users.dbHandler.DBUserHandler;
import users.document.readonly.CreateDocumentServicePanel;
import users.document.readonly.DocumentListPanel;
import users.document.updatable.NewDocumentInformationPanel;
import users.healthFacility.FacilityListPanel;
import users.notification.CreateNotificationPanel;
import users.notification.NotificationListPanel;
import users.patient.PatientListPanel;
import users.staff.CreateStaffPanel;
import users.staff.StaffListPanel;

import javax.swing.*;

/**
 * users
 * Created by NhatLinh - 19127652
 * Date 5/8/2022 - 11:28 AM
 * Description: ...
 */
public class UserTab extends JTabbedPane {

    public UserTab(DBUserHandler handler, String role)
    {
        super();

        switch (role)
        {
            case "SYS" -> {
                JTabbedPane staffTab = new JTabbedPane(JTabbedPane.TOP);
                staffTab.add("View staffs", new StaffListPanel(() -> handler.getStaffs(DBUserHandler.INSPECTOR_VIEW_STAFF),
                        handler::updateStaff));
                staffTab.add("Create staff", new CreateStaffPanel(handler::createStaff,
                        () -> handler.getIDs(DBUserHandler.FACILITY_IDS),
                        () -> handler.getIDs(DBUserHandler.DEPARTMENT_IDS)));
                JTabbedPane notificationTab = new JTabbedPane(JTabbedPane.TOP);
                notificationTab.add("View notification", new NotificationListPanel(handler::getNotifications));
                notificationTab.add("Create notification", new CreateNotificationPanel(handler::createNotification));

                add("Staffs", staffTab);
                add("Facilities", new FacilityListPanel(handler::getFacilities, handler::updateFacility));
                add("Notification", notificationTab);
            }
            case "BN" -> {
                add("Patients", new PatientListPanel(() -> handler.getPatients(DBUserHandler.PATIENT_VIEW_PATIENT),
                        handler::updatePatient));
            }
            case "Thanh tra" -> {
                add("Patients", new PatientListPanel(() -> handler.getPatients(DBUserHandler.INSPECTOR_VIEW_PATIENT),
                        handler::updatePatient));
                add("Staffs", new StaffListPanel(() -> handler.getStaffs(DBUserHandler.INSPECTOR_VIEW_STAFF),
                        handler::updateStaff));
                add("Documents", new DocumentListPanel(handler::getDocuments,
                        new CreateDocumentServicePanel(() -> handler.getIDs(DBUserHandler.SERVICE_IDS),
                                () -> handler.getIDs(DBUserHandler.STAFF_IDS), handler::createDocumentService),
                        handler::deleteDocument, handler::deleteDocumentService));
                add("Facilities", new FacilityListPanel(handler::getFacilities, handler::updateFacility));
                add("Notifications", new NotificationListPanel(handler::getNotifications));
            }
            case "Cơ sở y tế" -> {
                JTabbedPane documentTab = new JTabbedPane(JTabbedPane.TOP);
                documentTab.add("View documents", new DocumentListPanel(handler::getDocuments,
                        new CreateDocumentServicePanel(() -> handler.getIDs(DBUserHandler.SERVICE_IDS),
                                () -> handler.getIDs(DBUserHandler.STAFF_IDS), handler::createDocumentService),
                        handler::deleteDocument, handler::deleteDocumentService));
                documentTab.add("Create new document", new NewDocumentInformationPanel(handler::createDocument,
                        () -> handler.getIDs(DBUserHandler.PATIENT_IDS),
                        () -> handler.getIDs(DBUserHandler.DOCTOR_IDS),
                        () -> handler.getIDs(DBUserHandler.DEPARTMENT_IDS),
                        () -> handler.getIDs(DBUserHandler.FACILITY_IDS)));

                add("Staffs", new StaffListPanel(() -> handler.getStaffs(DBUserHandler.STAFF_VIEW_STAFF),
                        handler::updateStaff));
                add("Documents", documentTab);
                add("Notifications", new NotificationListPanel(handler::getNotifications));
            }
            case "Y/Bác sĩ" -> {
                add("Staffs", new StaffListPanel(() -> handler.getStaffs(DBUserHandler.STAFF_VIEW_STAFF),
                        handler::updateStaff));
                add("Documents", new DocumentListPanel(handler::getDocuments,
                        new CreateDocumentServicePanel(() -> handler.getIDs(DBUserHandler.SERVICE_IDS),
                                () -> handler.getIDs(DBUserHandler.STAFF_IDS), handler::createDocumentService),
                        handler::deleteDocument, handler::deleteDocumentService));
                add("Patients", new PatientListPanel(() -> handler.getPatients(DBUserHandler.DOCTOR_VIEW_PATIENT),
                        handler::updatePatient));
                add("Notifications", new NotificationListPanel(handler::getNotifications));
            }
            case "Nghiên cứu" -> {
                add("Staffs", new StaffListPanel(() -> handler.getStaffs(DBUserHandler.STAFF_VIEW_STAFF),
                        handler::updateStaff));
                add("Documents", new DocumentListPanel(handler::getDocuments,
                        new CreateDocumentServicePanel(() -> handler.getIDs(DBUserHandler.SERVICE_IDS),
                                () -> handler.getIDs(DBUserHandler.STAFF_IDS), handler::createDocumentService),
                        handler::deleteDocument, handler::deleteDocumentService));
                add("Notifications", new NotificationListPanel(handler::getNotifications));
            }
        }

    }
}