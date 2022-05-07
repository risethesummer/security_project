package users.patient;

import users.common.RecordViewPanel;
import users.dao.DBRecord;
import users.dao.Patient;

import javax.swing.*;

/**
 * users.patient
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 4:21 PM
 * Description: ...
 */
public class PatientViewPanel extends RecordViewPanel {
    private final PatientInformationPanel informationPanel = new PatientInformationPanel();
    public PatientViewPanel() {
        super();
        setMainSection(informationPanel);
    }
    @Override
    public void setRecord(DBRecord record) {
        informationPanel.setPatient((Patient)record);
    }
}
