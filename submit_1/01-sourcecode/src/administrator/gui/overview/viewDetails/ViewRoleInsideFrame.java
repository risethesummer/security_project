package administrator.gui.overview.viewDetails;

import administrator.dao.DBObject;
import administrator.dao.DBObjectType;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Vector;

/**
 * administrator.gui.overview.viewDetails
 * Created by NhatLinh - 19127652
 * Date 3/31/2022 - 12:39 PM
 * Description: ...
 */
public class ViewRoleInsideFrame extends ViewInsideObjectsFrame {

    private final JList<String> showRoles = new JList<>();

    public ViewRoleInsideFrame(String title) {
        super(title);
    }

    public void addComponent()
    {
        JPanel inFrame = new JPanel(new GridLayout(1, 2));
        JPanel user = new JPanel(new BorderLayout());
        JScrollPane userScroll = new JScrollPane(showObjects, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        user.add(new JTextField("User"), BorderLayout.PAGE_START);
        user.add(userScroll, BorderLayout.CENTER);
        JPanel role = new JPanel(new BorderLayout());
        JScrollPane roleScroll = new JScrollPane(showRoles, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        role.add(new JTextField("Role"), BorderLayout.PAGE_START);
        role.add(roleScroll, BorderLayout.CENTER);
        inFrame.add(user);
        inFrame.add(role);
        getContentPane().add(inFrame, BorderLayout.CENTER);
    }

    public void setObjects(String name, Collection<DBObject> objs)
    {
        setTitle(name);
        Vector<String> userData = new Vector<>();
        Vector<String> roleData = new Vector<>();
        for (DBObject object : objs)
        {
            if (object.getDBType() == DBObjectType.USER)
                userData.add(object.getName());
            else
                roleData.add(object.getName());
        }
        SwingUtilities.invokeLater(() ->
        {
            showObjects.setListData(userData);
            showRoles.setListData(roleData);
        });
    }
}
