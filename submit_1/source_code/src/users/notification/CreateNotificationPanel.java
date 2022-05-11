package users.notification;

import main.MainFrame;
import users.dao.Notification;

import javax.swing.*;
import java.awt.*;
import java.util.function.Predicate;

/**
 * users.notification
 * Created by NhatLinh - 19127652
 * Date 5/11/2022 - 2:22 AM
 * Description: ...
 */
public class CreateNotificationPanel extends JPanel {

    private final JTextArea contentField = new JTextArea();
    private final JTextField locationField = new JTextField();

    private JComboBox<String> labelCbb = new JComboBox<>(new String[]{
            "YBS",
            "YBS:noi,ngoai:ctt,tt",
            "YBS:noi,sau:tt",
            "GDCSYT",
            "GDCSYT:noi,ngoai:tt",
            "GDS"
    });

    private JButton createBtn = new JButton("Confirm to create");

    public CreateNotificationPanel(Predicate<Notification> createNotification)
    {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        labelCbb.setSelectedIndex(0);
        createBtn.setAlignmentX(CENTER_ALIGNMENT);
        createBtn.addActionListener(e -> {
            if (contentField.getText().isBlank() || locationField.getText().isBlank())
                JOptionPane.showMessageDialog(this, "The fields may not be blank");
            else
            {
                if (createNotification.test(new Notification(contentField.getText(), null, locationField.getText(), (String)labelCbb.getSelectedItem())))
                    JOptionPane.showMessageDialog(this, "Create the notification successfully");
                else
                    JOptionPane.showMessageDialog(this, "Failed to create the notification");
            }
        });


        MainFrame.addComponent(this, contentField, "Content");
        MainFrame.addComponent(this, locationField, "Location");
        MainFrame.addComponent(this, labelCbb, "Label");
        MainFrame.addComponent(this, createBtn);
    }
}
