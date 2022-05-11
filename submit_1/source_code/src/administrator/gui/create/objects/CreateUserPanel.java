package administrator.gui.create.objects;

import administrator.dao.user.UserFull;
import administrator.dbHandler.IDBAHandler;

import javax.swing.*;

/**
 * administrator.gui.create
 * Created by NhatLinh - 19127652
 * Date 3/26/2022 - 2:48 PM
 * Description: ...
 */
public class CreateUserPanel extends CreateObjectPanel {


    public CreateUserPanel(IDBAHandler handler, IDBAHandler roleHandler) {
        super(handler, roleHandler);

        commonSection.setBorder(BorderFactory.createTitledBorder("Common user?"));
        insideObjsPanel.setBorder(BorderFactory.createTitledBorder("Roles"));
        confirmButton.addActionListener(e -> {
            try
            {
                if (checkInputs())
                {
                    String confirmed = confirmUserName.getText();
                    if (handler.checkNameExists(confirmed))
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "The username has already existed!"));
                    else
                    {
                        if (handler.createObject(new UserFull(confirmed, getPassword(), getChoices().stream().toList())))
                            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Create user successfully"));
                        else
                            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Failed to create the user!"));
                    }
                }
                else
                    JOptionPane.showMessageDialog(this, "The inputs may not be empty!");
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error! Please, try it again!");
            }

        });
    }
}
