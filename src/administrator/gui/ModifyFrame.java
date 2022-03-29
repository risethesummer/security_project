package administrator.gui;

import administrator.gui.userOnDB.viewDB.GrantRevokePanel;

import javax.swing.*;

/**
 * administrator.gui
 * Created by NhatLinh - 19127652
 * Date 3/14/2022 - 3:40 PM
 * Description: ...
 */
public class ModifyFrame extends JFrame {

    private GrantRevokePanel grantRevokePanel = new GrantRevokePanel();
    public ModifyFrame(String id)
    {
        JTabbedPane tab = new JTabbedPane(JTabbedPane.LEFT);
        tab.add("Role", grantRevokePanel);
        getContentPane().add(tab);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        pack();
    }

}
