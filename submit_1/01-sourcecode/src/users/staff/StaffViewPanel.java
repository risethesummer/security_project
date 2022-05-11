package users.staff;

import common.gui.SearchPanel;
import users.common.RecordViewPanel;
import users.dao.DBRecord;
import users.dao.Patient;
import users.dao.Staff;
import users.patient.PatientInformationPanel;

import java.util.function.Predicate;

/**
 * users.staff
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 1:47 PM
 * Description: ...
 */
public class StaffViewPanel extends RecordViewPanel {

    private final StaffInformationPanel informationPanel;
    public StaffViewPanel(Predicate<Staff> update) {
        super();
        informationPanel =  new StaffInformationPanel(update);
        setMainSection(informationPanel);
    }
    @Override
    public void setRecord(DBRecord record) {
        informationPanel.setStaff((Staff) record);
    }
}
