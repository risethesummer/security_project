package users.dbHandler;

import common.handler.DBHandler;
import users.dao.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * users.dbHandler
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 2:25 PM
 * Description: ...
 */
public class DBUserHandler {
    protected final String userName;
    protected final String password;

    public final static String INSPECTOR_VIEW_PATIENT = "C##QLKCB.THANH_TRA_XEM_BENH_NHAN";
    public final static String INSPECTOR_VIEW_STAFF = "C##QLKCB.THANH_TRA_XEM_NHAN_VIEN";
    public final static String PATIENT_VIEW_PATIENT = "C##QLKCB.BENH_NHAN_XEM_BENH_NHAN";
    public final static String STAFF_VIEW_STAFF = "C##QLKCB.NHAN_VIEN_XEM_NHAN_VIEN";
    public final static String DOCTOR_VIEW_PATIENT = "C##QLKCB.VIEW_BAC_SI_XEM_BENH_NHAN";
    public final static String SERVICE_IDS = "C##QLKCB.DICH_VU_IDS";
    public final static String STAFF_IDS = "C##QLKCB.NHAN_VIEN_IDS";
    public final static String PATIENT_IDS = "C##QLKCB.BENH_NHAN_IDS";
    public final static String DOCTOR_IDS = "C##QLKCB.BAC_SI_IDS";
    public final static String DEPARTMENT_IDS = "C##QLKCB.KHOA_IDS";
    public final static String FACILITY_IDS = "C##QLKCB.CSYT_IDS";

    public DBUserHandler(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
    }

    public Iterable<DBRecord> getPatients(String view)
    {
        try
        {
            try (Connection connection = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + view);
                 ResultSet resultSet = statement.executeQuery())
            {
                List<DBRecord> patients = new ArrayList<>();
                while (resultSet.next())
                {
                    patients.add(new Patient(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getDate(5),
                            resultSet.getString(6),
                            resultSet.getString(7),
                            resultSet.getString(8),
                            resultSet.getString(9),
                            resultSet.getString(10),
                            resultSet.getString(11),
                            resultSet.getString(12)));
                }
                return patients;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public Iterable<DBRecord> getStaffs(String view)
    {
        try
        {
            try (Connection connection = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + view);
                 ResultSet resultSet = statement.executeQuery())
            {
                List<DBRecord> staffs = new ArrayList<>();
                while (resultSet.next())
                {
                    staffs.add(new Staff(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getDate(5),
                            resultSet.getString(6),
                            resultSet.getString(7),
                            resultSet.getString(8),
                            resultSet.getString(9),
                            resultSet.getString(10)));
                }
                return staffs;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public boolean createNotification(Notification notification)
    {
        try
        {
            try (Connection connection = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement alterSession = connection.prepareStatement("ALTER SESSION SET container = XEPDB1");
                 PreparedStatement statement = connection.prepareStatement(
                         "INSERT INTO C##QLKCB.THONGBAO VALUES (?, SYSTIMESTAMP, ?, CHAR_TO_LABEL('emp_ols_pol', ?))"))
            {
                alterSession.execute();
                statement.setString(1, notification.content());
                statement.setString(2, notification.location());
                statement.setString(3, notification.label());
                statement.execute();
                return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public Iterable<DBRecord> getNotifications()
    {
        try
        {
            try (Connection connection = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement alterSession = connection.prepareStatement("ALTER SESSION SET container = XEPDB1");
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM C##QLKCB.THONGBAO"))
            {
                alterSession.execute();
                List<DBRecord> notifications = new ArrayList<>();
                try (ResultSet resultSet = statement.executeQuery())
                {
                    while (resultSet.next())
                    {
                        notifications.add(new Notification(
                                resultSet.getString(1),
                                resultSet.getTimestamp(2).toLocalDateTime(),
                                resultSet.getString(3),
                                null
                        ));
                    }
                }

                return notifications;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


    public Iterable<DBRecord> getFacilities()
    {
        try
        {
            try (Connection connection = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM C##QLKCB.CSYT");
                 ResultSet resultSet = statement.executeQuery())
            {
                List<DBRecord> facilities = new ArrayList<>();
                while (resultSet.next())
                {
                    facilities.add(new Facility(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4)));
                }
                return facilities;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public Collection<String> getIDs(String view)
    {
        try
        {
            try (Connection connection = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + view);
                 ResultSet resultSet = statement.executeQuery())
            {
                List<String> ids = new ArrayList<>();
                while (resultSet.next())
                    ids.add(resultSet.getString(1));
                return ids;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
  /*  public Collection<String> getServiceIDs()
    {

    }

    public Collection<String> getStaffIDs()
    {

    }

    public Collection<String> getPatientIDs()
    {

    }

    public Collection<String> getDoctorIDs()
    {

    }

    public Collection<String> getDepartmentIDs()
    {

    }

    public Collection<String> getFacilityIDs()
    {

    }*/

    public Iterable<DBRecord> getDocuments()
    {
        try
        {
            try (Connection connection = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM C##QLKCB.HSBA");
                 ResultSet resultSet = statement.executeQuery())
            {
                List<DBRecord> documents = new ArrayList<>();
                while (resultSet.next())
                {
                    String documentID = resultSet.getString(1);
                    List<DocumentService> services = new ArrayList<>();
                    try (PreparedStatement detailsStatement = connection.prepareStatement("SELECT h.MADV, h.NGAY, h.MAKTV, h.KETQUA FROM C##QLKCB.HSBA_DV h WHERE h.MAHSBA = ?"))
                    {
                        detailsStatement.setString(1, documentID);
                        try (ResultSet detailsSet = detailsStatement.executeQuery())
                        {
                            while (detailsSet.next())
                            {
                                services.add(new DocumentService(
                                        detailsSet.getString(1),
                                        detailsSet.getDate(2),
                                        detailsSet.getString(3),
                                        detailsSet.getString(4))
                                );
                            }
                        }
                    }

                    documents.add(new Document(
                        documentID,
                        resultSet.getString(2),
                        resultSet.getDate(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getString(8), services));
                }
                return documents;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public boolean createDocument(Document document)
    {
        try
        {
            try (Connection connection = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = connection.prepareStatement(
                         "INSERT INTO C##QLKCB.HSBA VALUES (?, ?, ?, ?, ?, ?, ?, ?)"))
            {
                statement.setString(1, document.id());
                statement.setString(2, document.patientID());
                statement.setDate(3, new Date(document.date().getTime()));
                statement.setString(4, document.diagnose());
                statement.setString(5, document.dortorID());
                statement.setString(6, document.departmentID());
                statement.setString(7, document.facilityID());
                statement.setString(8, document.conclusion());
                return statement.executeUpdate() > 0;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createDocumentService(String documentID, DocumentService service)
    {
        try
        {
            try (Connection connection = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = connection.prepareStatement(
                         "INSERT INTO C##QLKCB.HSBA_DV VALUES (?, ?, ?, ?, ?)"))
            {
                statement.setString(1, documentID);
                statement.setString(2, service.serviceID());
                statement.setDate(3, new Date(service.date().getTime()));
                statement.setString(4, service.technicianID());
                statement.setString(5, service.result());
                return statement.executeUpdate() > 0;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDocument(String documentID)
    {
        try
        {
            try (Connection connection = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = connection.prepareStatement(
                         "DELETE FROM C##QLKCB.HSBA WHERE MAHSBA = ?"))
            {
                statement.setString(1, documentID);
                return statement.executeUpdate() > 0;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDocumentService(String documentID, DocumentService service)
    {
        try
        {
            try (Connection connection = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = connection.prepareStatement(
                         "DELETE FROM C##QLKCB.HSBA_DV WHERE MAHSBA = ? AND MADV = ? AND NGAY = ?"))
            {
                statement.setString(1, documentID);
                statement.setString(2, service.serviceID());
                statement.setDate(3, new Date(service.date().getTime()));
                return statement.executeUpdate() > 0;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFacility(Facility facility) {

        try
        {
            try (Connection connection = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = connection.prepareStatement(
                         "UPDATE C##QLKCB.CSYT SET TENCSYT = ?, DCCSYT = ?, SDTCSYT = ? WHERE MACSYT = ?"))
            {
                statement.setString(1, facility.name());
                statement.setString(2, facility.address());
                statement.setString(3, facility.phone());
                statement.setString(4, facility.id());
                return statement.executeUpdate() > 0;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateStaff(Staff staff) {

        try
        {
            try (Connection connection = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = connection.prepareStatement(
                         "UPDATE C##QLKCB.NHAN_VIEN_XEM_NHAN_VIEN " +
                                 "SET HOTEN = ?, PHAI = ?, NGAYSINH = ?, QUEQUAN = ?, SODT = ? " +
                                 "WHERE MANV = ?"))
            {
                statement.setString(1, staff.name());
                statement.setString(2, staff.sex());
                statement.setDate(3, new Date(staff.dob().getTime()));
                statement.setString(4, staff.homeTown());
                statement.setString(5, staff.phone());
                statement.setString(6, staff.id());
                if (statement.executeUpdate() > 0)
                {
                    if (staff.idCard() != null)
                    {
                        try (CallableStatement idCardStatement = connection.prepareCall("{CALL C##QLKCB.CHINHSUA_NHANVIEN_CMND(?)}"))
                        {
                            idCardStatement.setString(1, staff.idCard());
                            idCardStatement.execute();
                        }
                    }
                    return true;
                }
                return false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePatient(Patient patient) {

        try
        {
            try (Connection connection = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = connection.prepareStatement(
                         "UPDATE C##QLKCB.BENH_NHAN_XEM_BENH_NHAN " +
                                 "SET TENBN = ?, NGAYSINH = ?, SONHA = ?, TENDUONG = ?, QUANHUYEN = ?, TINHTP = ? " +
                                 "WHERE MABN = ?"))
            {
                statement.setString(1, patient.name());
                statement.setDate(2, new Date(patient.dob().getTime()));
                statement.setString(3, patient.number());
                statement.setString(4, patient.street());
                statement.setString(5, patient.district());
                statement.setString(6, patient.city());
                statement.setString(7, patient.id());
                if (statement.executeUpdate() > 0)
                {
                    if (patient.idCard() != null)
                    {
                        try (CallableStatement idCardStatement = connection.prepareCall("{CALL C##QLKCB.CHINHSUA_BENHNHAN_CMND(?)}"))
                        {
                            idCardStatement.setString(1, patient.idCard());
                            idCardStatement.execute();
                        }
                    }
                    return true;
                }
                return false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createStaff(Staff staff) {
        try
        {
            try (Connection connection = DBHandler.getInstance().getConnection(userName, password);
                 CallableStatement statement = connection.prepareCall(
                         "{CALL C##QLKCB.THEM_NHANVIEN(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}"))
            {
                statement.setString(1, staff.id());
                statement.setString(2, staff.name());
                statement.setString(3, staff.sex());
                statement.setString(4, staff.idCard());
                statement.setDate(5, new Date(staff.dob().getTime()));
                statement.setString(6, staff.homeTown());
                statement.setString(7, staff.phone());
                statement.setString(8, staff.facilityID());
                statement.setString(9, staff.role());
                statement.setString(10, staff.major());
                statement.setString(11, "C##" + staff.id());
                return statement.executeUpdate() > 0;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}