package users.document.readonly;

import users.common.RecordViewPanel;
import users.dao.DBRecord;
import users.dao.Document;

/**
 * users.document
 * Created by NhatLinh - 19127652
 * Date 5/7/2022 - 1:20 PM
 * Description: ...
 */
public class DocumentViewPanel extends RecordViewPanel {

    private final DocumentInformationPanel informationPanel = new DocumentInformationPanel();
    public DocumentViewPanel() {
        super();
        setMainSection(informationPanel);
    }
    @Override
    public void setRecord(DBRecord record) {
        informationPanel.setDocument((Document)record);
    }
}
