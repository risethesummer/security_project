package common.gui;

import common.dtos.LoginInformation;

import javax.swing.*;
import java.util.function.Function;

/**
 * common.gui
 * Created by NhatLinh - 19127652
 * Date 4/8/2022 - 12:24 AM
 * Description: ...
 */
public class LoginPanel extends UserPanel {

    public LoginPanel(Function<LoginInformation, Boolean> onLogin)
    {
        super();
        confirmButton.setText("Login");
        confirmButton.addActionListener(e -> {
            if (checkInputs())
            {
                if (!onLogin.apply(new LoginInformation(getUsername(), getPassword())))
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Failed to login!"));
            }
            else
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "The information may not be empty!"));
        });
    }
}
