package users.staff;

import common.gui.table.cells.DatePickerCell;
import main.MainFrame;
import users.common.TwoHorizontalComponentSection;
import users.dao.Staff;

import javax.swing.*;
import java.sql.Date;
import java.time.Instant;
import java.util.function.Predicate;

/**
 * users.staff
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 4:37 PM
 * Description: ...
 */
public class StaffInformationPanel extends JPanel {


    private final JTextField id = new JTextField("");
    private final JTextField oldName = new JTextField("");
    private final JTextField newName = new JTextField("");
    private final JTextField oldSex = new JTextField("");
    private final JComboBox<String> newSex = new JComboBox<>(new String[]{
            "Nam", "Nữ"
    });
    private final JTextField oldDob = new JTextField("");
    private final DatePickerCell newDob = new DatePickerCell(false);
    private final JTextField oldIdCard = new JTextField("");
    private final JTextField newIdCard = new JTextField("");
    private final JTextField oldHomeTown = new JTextField("");
    private final JTextField newHomeTown = new JTextField("");
    private final JTextField oldPhone = new JTextField("");
    private final JTextField newPhone = new JTextField("");
    private final JTextField facilityID = new JTextField("");
    private final JTextField role = new JTextField("");
    private final JTextField major = new JTextField("");

    public StaffInformationPanel(Predicate<Staff> update)
    {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        MainFrame.addComponentAndDisable(this, id, "Staff ID");

        TwoHorizontalComponentSection nameSection = new TwoHorizontalComponentSection(
                oldName, "Current name", newName, "New name"
        );
        MainFrame.addComponent(this, nameSection, "Staff name");

        TwoHorizontalComponentSection sexSection = new TwoHorizontalComponentSection(
                oldSex, "Current sex", newSex, "New sex"
        );
        MainFrame.addComponent(this, sexSection, "Sex");

        TwoHorizontalComponentSection dobSection = new TwoHorizontalComponentSection(
                oldDob, "Current date of birth", (JComponent)newDob.getComponent(), "New date of birth"
        );
        MainFrame.addComponent(this, dobSection, "Day of birth");

        TwoHorizontalComponentSection idCardSection = new TwoHorizontalComponentSection(
                oldIdCard, "Current identity card", newIdCard, "New identity card"
        );
        MainFrame.addComponent(this, idCardSection, "Identity card");

        TwoHorizontalComponentSection homeTownSection = new TwoHorizontalComponentSection(
                oldHomeTown, "Current hometown", newHomeTown, "New hometown"
        );
        MainFrame.addComponent(this, homeTownSection, "Home town");

        TwoHorizontalComponentSection phoneSection = new TwoHorizontalComponentSection(
                oldPhone, "Current phone", newPhone, "New phone"
        );
        MainFrame.addComponent(this, phoneSection, "Phone");

        MainFrame.addComponentAndDisable(this, facilityID, "Facility ID");
        MainFrame.addComponentAndDisable(this, role, "Role");
        MainFrame.addComponentAndDisable(this, major, "Department ID");

        JButton updateBtn = new JButton("Update information");
        updateBtn.setAlignmentX(CENTER_ALIGNMENT);
        updateBtn.addActionListener(e -> {
            Object[] options = {
                    "Yes, I want to update the information",
                    "No, I don't"};
            int n = JOptionPane.showOptionDialog(this,
                    "Do you really want to update the information?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (n == 0)
            {
                if (update.test(getUpdate()))
                {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Update the information successfully"));
                }
                else
                {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Failed to update"));
                }
            }
        });

        MainFrame.addComponent(this, updateBtn);
    }

    public Staff getUpdate()
    {
        String name = newName.getText().isBlank() ? oldName.getText() : newName.getText();
        String sex = newSex.getSelectedIndex() == -1 ? oldSex.getText() : (String)newSex.getSelectedItem();
        java.util.Date dob = newDob.getDateModel().getValue().after(Date.from(Instant.now())) ? Date.valueOf(oldDob.getText()) : newDob.getDateModel().getValue();
        String idCard = newIdCard.getText().isBlank() ? oldIdCard.getText() : newIdCard.getText();
        String homeTown = newHomeTown.getText().isBlank() ? oldHomeTown.getText() : newHomeTown.getText();
        String phone = newPhone.getText().isBlank() ? oldPhone.getText() : newPhone.getText();

        return new Staff(id.getText(), name, sex, idCard, dob, homeTown, phone, null, null, null);
    }

    public void setStaff(Staff staff)
    {
        id.setText(staff.id());

        oldName.setText(staff.name());
        newName.setText("");

        oldSex.setText(staff.sex());
        newSex.setSelectedIndex(-1);

        oldDob.setText(staff.dob().toString());
        newDob.getDateModel().setValue(Date.from(Instant.now()));

        oldIdCard.setText(staff.idCard());
        newIdCard.setText("");

        oldHomeTown.setText(staff.homeTown());
        newHomeTown.setText("");

        oldPhone.setText(staff.phone());
        newPhone.setText("");

        facilityID.setText(staff.facilityID());
        role.setText(staff.role());
        major.setText(staff.major());
    }
}
