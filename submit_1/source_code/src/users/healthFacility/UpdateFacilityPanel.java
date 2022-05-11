package users.healthFacility;

import main.MainFrame;
import users.common.TwoHorizontalComponentSection;
import users.dao.Facility;

import javax.swing.*;
import java.util.function.Predicate;

public class UpdateFacilityPanel extends JPanel {
    private final JTextField id = new JTextField("");
    private final JTextField oldName = new JTextField("");
    private final JTextField newName = new JTextField("");
    private final JTextField oldAddress = new JTextField("");
    private final JTextField newAddress = new JTextField("");
    private final JTextField oldPhone = new JTextField("");
    private final JTextField newPhone = new JTextField("");

    public UpdateFacilityPanel(Predicate<Facility> update)
    {
        super();
        JButton updateBtn = new JButton("Update the facility");
        updateBtn.setAlignmentX(CENTER_ALIGNMENT);
        updateBtn.addActionListener(e -> {
            Object[] options = {
                    "Yes, I want to update the facility",
                    "No, I don't"};
            int n = JOptionPane.showOptionDialog(this,
                    "Do you really want to update the facility?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (n == 0)
            {
                if (update.test(getFacility()))
                {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Update the facility successfully"));
                }
                else
                {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Failed to update the facility"));
                }
            }
        });
        oldName.setEditable(false);
        oldAddress.setEditable(false);
        oldPhone.setEditable(false);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        MainFrame.addComponentAndDisable(this, id, "ID");
        MainFrame.addComponent(this,
                new TwoHorizontalComponentSection(oldName, "Old name", newName, "New name"),
                "Name");
        MainFrame.addComponent(this,
                new TwoHorizontalComponentSection(oldAddress, "Old address", newAddress, "New address"),
                "Address");
        MainFrame.addComponent(this,
                new TwoHorizontalComponentSection(oldPhone, "Old phone", newPhone, "New phone"),
                "Phone");
        MainFrame.addComponent(this, updateBtn);
    }

    public void resetInputs()
    {
        newName.setText("");
        newAddress.setText("");
        newPhone.setText("");
    }

    public Facility getFacility()
    {
        String name = newName.getText().isBlank() ? oldName.getText() : newName.getText();
        String address = newAddress.getText().isBlank() ? oldAddress.getText() : newAddress.getText();
        String phone = newPhone.getText().isBlank() ? oldPhone.getText() : newPhone.getText();
        return new Facility(id.getText(), name, address, phone);
    }

    public void setFacility(Facility facility)
    {
        id.setText(facility.id());
        oldName.setText(facility.name());
        oldAddress.setText(facility.address());
        oldPhone.setText(facility.phone());
        resetInputs();
    }
}
