package administrator.gui.privileges.userRole;

import common.gui.WrapLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * administrator.gui.userOnDB.viewDB
 * Created by NhatLinh - 19127652
 * Date 3/14/2022 - 3:27 PM
 * Description: ...
 */
public abstract class SearchingDBObjectPanel extends JPanel {
    protected final JPanel contentSection = new JPanel(new WrapLayout());
    protected final JTextField inputField = new JTextField();
    public SearchingDBObjectPanel()
    {
        super(new BorderLayout());
        inputField.setBorder(BorderFactory.createTitledBorder("Search"));
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                getHint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                getHint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                getHint();
            }
        });
        add(inputField, BorderLayout.PAGE_START);
        JScrollPane contentScroll = new JScrollPane(contentSection, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(contentScroll, BorderLayout.CENTER);
    }

    protected abstract void getHint();
}
