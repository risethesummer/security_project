package users.document.readonly;

import users.common.RecordViewPanel;
import users.dao.DBRecord;
import users.dao.Document;
import users.dao.DocumentService;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * users.document
 * Created by NhatLinh - 19127652
 * Date 5/7/2022 - 1:20 PM
 * Description: ...
 */
public class DocumentViewPanel extends RecordViewPanel {

    private DocumentInformationPanel informationPanel;
    public DocumentViewPanel(CreateDocumentServicePanel createDocumentServicePanel,
                             Predicate<String> onDelDocument,
                             BiPredicate<String, DocumentService> onDelService) {
        super();
        informationPanel = new DocumentInformationPanel(createDocumentServicePanel,
                id -> {
                    boolean state = onDelDocument.test(id);
                    if (state)
                        setMainSection(informationPanel);
                    return state;},
                onDelService);
        setMainSection(informationPanel);
    }
    @Override
    public void setRecord(DBRecord record) {
        informationPanel.setDocument((Document)record);
    }
}
