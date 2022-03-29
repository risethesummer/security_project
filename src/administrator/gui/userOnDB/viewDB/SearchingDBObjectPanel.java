package administrator.gui.userOnDB.viewDB;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

/**
 * administrator.gui.userOnDB.viewDB
 * Created by NhatLinh - 19127652
 * Date 3/14/2022 - 3:27 PM
 * Description: ...
 */
public class SearchingDBObjectPanel extends JPanel {

    protected final JPanel contentSection = new JPanel(new FlowLayout(FlowLayout.LEADING));
    private final JTextField inputField = new JTextField();
    public SearchingDBObjectPanel()
    {
        super(new BorderLayout());
        add(inputField, BorderLayout.PAGE_START);
        add(contentSection, BorderLayout.CENTER);
    }

}
