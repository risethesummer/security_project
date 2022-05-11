package main;

import administrator.dbHandler.roleAndUser.DBARoleHandler;
import administrator.dbHandler.roleAndUser.DBAUserHandler;
import administrator.dbHandler.table.TableHandler;
import administrator.dbHandler.table.ViewHandler;
import administrator.gui.AdminFrame;
import common.dtos.LoginInformation;
import common.gui.LoginPanel;
import common.handler.DBHandler;
import users.UserFrame;
import users.dbHandler.DBUserHandler;
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
        getContentPane().add(new LoginPanel(this::login));
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
        String role = DBHandler.getInstance().login(username, password);
        if (role != null)
        {
            DBUserHandler userHandler = new DBUserHandler(username, password);
            if (role.equals("SYS"))
            {
                TableHandler tableHandler = new TableHandler(username, password);
                new AdminFrame(
                        () -> setVisible(true),
                        new DBAUserHandler(username, password),
                        new DBARoleHandler(username, password),
                        tableHandler,
                        new ViewHandler(username, password, tableHandler),
                        userHandler);
                setVisible(false);
                return true;
            }
            else
            {
                new UserFrame(
                        () -> setVisible(true),
                        userHandler,
                        role
                );
                setVisible(false);
                return true;
            }
        }
        return false;
    }
}
