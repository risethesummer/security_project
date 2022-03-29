package common.gui;

import administrator.gui.ImageRepository;

import javax.swing.*;
import java.awt.*;

/**
 * administrator.gui.create
 * Created by NhatLinh - 19127652
 * Date 3/26/2022 - 1:10 PM
 * Description: ...
 */
public class UserPanel extends JPanel {

    protected final JTextField userNameField = new JTextField();
    protected final JPasswordField passwordField = new JPasswordField();
    protected final JButton confirmButton = new JButton("Confirm");
    protected final JLabel userNameLabel = new JLabel("Username");
    protected final JPanel passwordSection = new JPanel(new BorderLayout());
    private boolean isShowing = false;

    public UserPanel()
    {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        userNameField.setPreferredSize(new Dimension(100, userNameField.getPreferredSize().height));
        JPanel userSection = new JPanel(new BorderLayout());
        userSection.add(userNameLabel, BorderLayout.LINE_START);
        userSection.add(userNameField, BorderLayout.CENTER);
        userSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, userSection.getPreferredSize().height));

        passwordSection.add(new Label("Password"), BorderLayout.LINE_START);
        passwordSection.add(passwordField, BorderLayout.CENTER);
        ButtonImage showPassword = new ButtonImage("show.png", ImageRepository.SMALL, "Show password");
        showPassword.addActionListener(e -> {
            if (isShowing)
            {
                showPassword.setIcon("show.png");
                passwordField.setEchoChar('•');
                isShowing = false;
            }
            else
            {
                showPassword.setIcon("hide.png");
                passwordField.setEchoChar((char)0);
                isShowing = true;
            }
        });
        passwordSection.add(showPassword, BorderLayout.LINE_END);
        passwordSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, passwordSection.getPreferredSize().height));
        confirmButton.setAlignmentX(CENTER_ALIGNMENT);


        add(Box.createVerticalStrut(20));
        add(userSection);
        add(Box.createVerticalStrut(10));
        add(passwordSection);
        add(Box.createVerticalStrut(10));
        add(confirmButton);
        add(Box.createVerticalStrut(20));
        /*
        add(Box.createVerticalStrut(20), BorderLayout.PAGE_START);
        add(Box.createVerticalStrut(20), BorderLayout.PAGE_END);*/
    }

    public boolean checkInputs()
    {
        return !userNameField.getText().isBlank() && !userNameField.getText().contains(" ") && passwordField.getPassword().length > 0;
    }

    public String getUsername()
    {
        return userNameField.getText();
    }

    public String getPassword()
    {
        return String.valueOf(passwordField.getPassword());
    }
}
