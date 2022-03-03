package administrator.gui;

import administrator.dao.DBObject;
import administrator.dao.User;
import administrator.dbHandler.IUserDBHandler;
import administrator.gui.table.*;

import javax.swing.*;
import java.util.List;
import java.util.function.Function;

/**
 * administrator.gui
 * Created by NhatLinh - 19127652
 * Date 2/21/2022 - 9:07 PM
 * Description: ...
 */
public class ViewUsersPanel extends OverviewPanel {

    private final IUserDBHandler dbHandler;

    public ViewUsersPanel(IUserDBHandler dbHandler)
    {
        super(new String[] {
                "User", "Roles", "Modify roles", "Permissions", "Grant permission", "Deny permission", "Delete user"
        });
        this.dbHandler = dbHandler;
        createButton.setText("Create new user");

    }

    public void showUsers(List<User> users) {
        if (users != null && !users.isEmpty())
        {
            for (User user : users)
            {
                StringBuilder rolesText = new StringBuilder();
                for (DBObject role : user.getRoles())
                    rolesText.append(role.getName() + '\n');
                //remove the last end line character
                rolesText.deleteCharAt(rolesText.length() - 1);
                NColumnsPanel userRow = new NColumnsPanel(new ICell[] {
                        new TextCell(user.getName()),
                        new MultipleLinesTextCell(rolesText.toString()),
                        new ButtonCell("Modify roles", user.getName(), null),
                        new ButtonCell("View permissions", user.getName(), null),
                        new ButtonCell("Grant permissions", user.getName(), null),
                        new ButtonCell("Deny permissions", user.getName(), null),
                        new ButtonCell("Delete permissions", user.getName(), null)
                });
            }
        }
    }

    @Override
    protected void getHint() {

    }
}
