package administrator.gui.userOnDB.viewDB;

import javax.swing.*;
import java.awt.*;

/**
 * administrator.gui.userOnDB.viewDB
 * Created by NhatLinh - 19127652
 * Date 3/14/2022 - 3:44 PM
 * Description: ...
 */
public class GrantRevokePanel extends JPanel {

    private SearchingDBObjectPanel grantPanel = new SearchingDBObjectPanel();
    private SearchingDBObjectPanel revokePanel = new SearchingDBObjectPanel();
    public GrantRevokePanel()
    {
        super(new GridLayout(1, 2));
        add(grantPanel);
        add(revokePanel);
    }
}