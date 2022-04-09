package administrator.gui.privileges.userRole;

import administrator.dao.DBObject;
import administrator.dbHandler.IDBAHandler;
import administrator.dbHandler.roleAndUser.IRoleHandler;
import administrator.gui.INameCriticalPanel;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Collection;

/**
 * administrator.gui.userOnDB.viewDB
 * Created by NhatLinh - 19127652
 * Date 3/14/2022 - 3:44 PM
 * Description: ...
 */
public class GrantRevokePanel extends JPanel {

    protected String name;
    private final INameCriticalPanel grantPanel;
    private final INameCriticalPanel revokePanel;
    protected final IDBAHandler userHandler;
    protected final IRoleHandler roleHandler;

    public GrantRevokePanel(IDBAHandler userHandler, IRoleHandler roleHandler) {
        super(new BorderLayout());
        this.userHandler = userHandler;
        this.roleHandler = roleHandler;
        grantPanel = new ShowDBObjectPanel("Grant");
        revokePanel = new ShowDBObjectPanel("Revoke");

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> update());

        JPanel contentSection = new JPanel(new GridLayout(1, 2));
        contentSection.add(grantPanel.getComponent());
        contentSection.add(revokePanel.getComponent());
        add(updateButton, BorderLayout.PAGE_START);
        add(contentSection, BorderLayout.CENTER);
    }

    public void setUserName(String name) {
        this.name = name;
        update();
    }

    public void update() {
        grantPanel.clear();
        revokePanel.clear();
        Collection<String> inside = getInsideObjs();
        if (inside != null && !inside.isEmpty()) {
            for (String roleName : inside) {
                revokePanel.add(roleName, getRevokeButton(roleName));
            }
        }

        Collection<String> outSide = getOutsideObjs();
        if (outSide != null && !outSide.isEmpty()) {
            for (String roleName : outSide) {
                grantPanel.add(roleName, getGrantButton(roleName));
            }
        }
        SwingUtilities.invokeLater(revokePanel::updateUI);
    }

    protected Collection<String> getInsideObjs()
    {
        return userHandler.getInsideObjectsToGrant(name).stream().map(DBObject::getName).toList();
    }

    protected Collection<String> getOutsideObjs()
    {
        return userHandler.getOutsideObjects(name).stream().map(DBObject::getName).toList();
    }

    public boolean revoke(String roleName)
    {
        return roleHandler.revoke(roleName, name);
    }

    private JButton getRevokeButton(String roleName)
    {
        JButton revokeButton = new JButton(roleName);
        revokeButton.addActionListener(actionEvent -> {
            if (revoke(roleName))
            {
                revokePanel.remove(roleName);
                grantPanel.add(roleName, getGrantButton(roleName));
            }
            else
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Failed to revoke!"));
            SwingUtilities.invokeLater(revokePanel::updateUI);
        });

        return revokeButton;
    }

    public boolean grant(String roleName)
    {
        return roleHandler.grant(roleName, name);
    }

    private JButton getGrantButton(String roleName)
    {
        JButton grantButton = new JButton(roleName);
        grantButton.addActionListener(actionEvent -> {
            if (grant(roleName))
            {
                grantPanel.remove(roleName);
                revokePanel.add(roleName, getRevokeButton(roleName));
            }
            else
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Failed to grant!"));
            SwingUtilities.invokeLater(revokePanel::updateUI);
        });
        return grantButton;
    }
}