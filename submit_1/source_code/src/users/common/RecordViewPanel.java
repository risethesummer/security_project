package users.common;

import users.dao.DBRecord;

import javax.swing.*;
import java.awt.*;

/**
 * users.common
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 2:31 PM
 * Description: ...
 */
public abstract class RecordViewPanel extends JPanel {

    private final JButton backBtn = new JButton("Back to list");
    private final JPanel cardPanel = new JPanel(new BorderLayout());

    public RecordViewPanel()
    {
        super(new BorderLayout());
        add(backBtn, BorderLayout.PAGE_START);
        add(cardPanel, BorderLayout.CENTER);
    }

    public void setMainSection(Container mainSection)
    {
        cardPanel.removeAll();
        cardPanel.add(mainSection, BorderLayout.CENTER);
        cardPanel.updateUI();
    }

    public void setBackCallback(Runnable backCallback)
    {
        backBtn.addActionListener(e -> backCallback.run());
    }

    public abstract void setRecord(DBRecord DBRecord);
}
