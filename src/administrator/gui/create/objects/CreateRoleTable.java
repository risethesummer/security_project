package administrator.gui.create.objects;

import administrator.dao.role.Role;
import administrator.dbHandler.IDBAHandler;

import javax.swing.*;

/**
 * administrator.gui.create
 * Created by NhatLinh - 19127652
 * Date 3/26/2022 - 5:14 PM
 * Description: ...
 */
public class CreateRoleTable extends CreateObjectPanel {

    public CreateRoleTable(IDBAHandler handler, IDBAHandler userHandler) {
        super(handler, userHandler);
        userNameLabel.setText("Role");
        remove(passwordSection);
        insideObjsPanel.setBorder(BorderFactory.createTitledBorder("Users"));
        confirmButton.addActionListener(e -> {
            try
            {
                if (checkInputs())
                {
                    if (userHandler.checkNameExists(getUsername()))
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "The role has already existed!"));
                    else
                    {
                        if (handler.createObject(new Role(getUsername(), getChoices().stream().toList())))
                            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Create the role successfully"));
                        else
                            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Failed to create the role!"));
                    }
                }
                else
                    JOptionPane.showMessageDialog(this, "The role name may not be ignored!");
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error! Please, try it again!");
            }

        });
    }

    @Override
    public boolean checkInputs()
    {
        return !userNameField.getText().isBlank() && !userNameField.getText().contains(" ");
    }
}
