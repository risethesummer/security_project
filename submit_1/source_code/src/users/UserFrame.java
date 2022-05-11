package users;

import common.gui.DisposableFrame;
import users.dbHandler.DBUserHandler;

import javax.swing.*;

/**
 * users
 * Created by NhatLinh - 19127652
 * Date 5/7/2022 - 3:17 PM
 * Description: ...
 */
public class UserFrame extends DisposableFrame {

    public UserFrame(Runnable onClose, DBUserHandler handler, String role)
    {
        super(onClose);
        getContentPane().add(new UserTab(handler, role));
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        //pack();
    }

    @Override
    public void dispose()
    {
        onDispose.run();
        super.dispose();
    }
}
