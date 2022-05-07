package main;

import administrator.dbHandler.roleAndUser.DBARoleHandler;
import administrator.dbHandler.roleAndUser.DBAUserHandler;
import administrator.dbHandler.table.TableHandler;
import administrator.dbHandler.table.ViewHandler;
import administrator.gui.AdminFrame;
import common.dtos.LoginInformation;
import common.handler.DBHandler;
import users.staff.StaffViewPanel;

import javax.swing.*;
import javax.swing.text.JTextComponent;
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
        //getContentPane().add(new LoginPanel(this::login));
        getContentPane().add(new StaffViewPanel());
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1000, 1000));
        setLocationRelativeTo(null);
        setTitle("Login");
        setVisible(true);
        pack();
    }

    public static void addComponent(Container container, Component component, String title)
    {
        addComponent(container, component, title, container.getComponentCount());
    }

    public static void addComponent(Container container, Component component, String title, int index)
    {
        ((JComponent)component).setBorder(BorderFactory.createTitledBorder(title));
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height));
        container.add(component, index);
    }

    public static void addComponent(Container container, Component component)
    {
        addComponent(container, component, container.getComponentCount());
    }

    public static void addComponent(Container container, Component component, int index)
    {
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height));
        container.add(component, index);
    }

    public static void addComponentAndDisable(Container container, JTextComponent component, String title)
    {
        component.setEditable(false);
        addComponent(container, component, title);
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
