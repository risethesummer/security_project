import administrator.dbHandler.roleAndUser.DBARoleHandler;
import administrator.dbHandler.roleAndUser.DBAUserHandler;
import administrator.dbHandler.table.TableHandler;
import administrator.dbHandler.table.ViewHandler;
import administrator.gui.AdminFrame;
import administrator.gui.privileges.table.SearchTablePanel;
import administrator.gui.privileges.userRole.GrantRevokePanel;
import common.dtos.LoginInformation;
import common.gui.LoginPanel;
import common.handler.DBHandler;

import javax.swing.*;
import java.awt.*;

/**
 * PACKAGE_NAME
 * Created by NhatLinh - 19127652
 * Date 4/8/2022 - 12:44 AM
 * Description: ...
 */
public class MainFrame extends JFrame {

    public MainFrame()
    {
        getContentPane().add(new LoginPanel(this::login));
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(500, 300));
        setLocationRelativeTo(null);
        setTitle("Login");
        setVisible(true);
        pack();
    }

    public boolean login(LoginInformation login)
    {
        String username = login.username();
        String password = login.password();
        if (DBHandler.getInstance().loginAsDBA(username, password))
        {
            TableHandler tableHandler = new TableHandler(username, password);
            new AdminFrame(
                    () -> {
                        setVisible(true);
                    },
                    new DBAUserHandler(username, password),
                    new DBARoleHandler(username, password),
                    tableHandler,
                    new ViewHandler(username, password, tableHandler));
            setVisible(false);
            return true;
        }
        return false;
    }
}
