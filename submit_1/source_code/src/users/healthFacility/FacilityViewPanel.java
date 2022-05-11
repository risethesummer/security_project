package users.healthFacility;

import users.common.RecordViewPanel;
import users.dao.DBRecord;
import users.dao.Facility;

import java.util.function.Predicate;

/**
 * users.healthFacility
 * Created by NhatLinh - 19127652
 * Date 5/7/2022 - 5:48 PM
 * Description: ...
 */
public class FacilityViewPanel extends RecordViewPanel {
    private final UpdateFacilityPanel updatePanel;
    public FacilityViewPanel(Predicate<Facility> update) {
        super();
        updatePanel = new UpdateFacilityPanel(update);
        setMainSection(updatePanel);
    }
    @Override
    public void setRecord(DBRecord record) {
        updatePanel.setFacility((Facility)record);
    }
}
