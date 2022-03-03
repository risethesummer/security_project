package administrator.gui;

import javax.swing.*;

/**
 * administrator
 * Created by NhatLinh - 19127652
 * Date 2/21/2022 - 8:14 PM
 * Description: ...
 */
public class AdminFrame extends JFrame {


    public AdminFrame()
    {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.add("Manage users", new ViewUsersPanel(null));
        tabbedPane.add("Manage roles", new ViewUsersPanel(null));

        getContentPane().add(tabbedPane);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        pack();
    }
}
