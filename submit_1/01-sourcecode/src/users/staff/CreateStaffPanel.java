package users.staff;

import common.gui.table.cells.ComboBoxCell;
import common.gui.table.cells.DatePickerCell;
import main.MainFrame;
import users.dao.Staff;

import javax.swing.*;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * users.staff
 * Created by NhatLinh - 19127652
 * Date 5/7/2022 - 5:05 PM
 * Description: ...
 */
public class CreateStaffPanel extends JPanel {
    private final JTextField id = new JTextField("");
    private final JTextField name = new JTextField("");
    private final JComboBox<String> sex = new JComboBox<>(new String[]{"Nam", "Nữ"});
    private final DatePickerCell dob = new DatePickerCell(false);
    private final JTextField idCard = new JTextField("");
    private final JTextField homeTown = new JTextField("");
    private final JTextField phone = new JTextField("");
    private final ComboBoxCell facilityID = new ComboBoxCell();
    private final JComboBox<String> role = new JComboBox<>(new String[]{
            "Inspector", "Facility", "Doctor", "Research"
    });
    private final ComboBoxCell departmentID = new ComboBoxCell();
    private final JButton createBtn = new JButton("Create staff");
    public CreateStaffPanel(Predicate<Staff> createStaff,
                                 Supplier<Collection<String>> onGetFacilities,
                                 Supplier<Collection<String>> onGetDepartments)
    {
        super();
        facilityID.setDropEvent(onGetFacilities);
        departmentID.setDropEvent(onGetDepartments);
        sex.setSelectedIndex(0);
        role.setSelectedIndex(0);
        createBtn.addActionListener(e -> {
            if (isInputNotValid())
            {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "You need to fill out all the fields"));
            }
            else
            {
                Object[] options = {
                        "Yes, I want to create the staff row",
                        "No, I don't"};
                int n = JOptionPane.showOptionDialog(this,
                        "Do you really want to create the service staff?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (n == 0)
                {
                    if (createStaff.test(getStaff()))
                    {
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(this, "Create the staff row successfully"));
                    }
                    else
                    {
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(this, "Failed to create the staff"));
                    }
                }
            }
        });
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        MainFrame.addComponent(this, id, "Patient ID");
        MainFrame.addComponent(this, name, "Full name");
        MainFrame.addComponent(this, sex, "Sex");
        MainFrame.addComponent(this, dob.getComponent(), "Day of birth");
        MainFrame.addComponent(this, idCard, "Identity card");
        MainFrame.addComponent(this, homeTown, "Home town");
        MainFrame.addComponent(this, phone, "Phone");
        MainFrame.addComponent(this, facilityID, "Facility ID");
        MainFrame.addComponent(this, role, "Role");
        MainFrame.addComponent(this, departmentID, "Department ID");
        MainFrame.addComponent(this, createBtn);
    }

    public boolean isInputNotValid()
    {
        return id.getText().isBlank() ||
                name.getText().isBlank() ||
                sex.getSelectedIndex() == -1 ||
                dob.getDateModel().getValue() == null ||
                idCard.getText().isBlank() ||
                homeTown.getText().isBlank() ||
                phone.getText().isBlank() ||
                facilityID.getSelectedItem() == null ||
                role.getSelectedIndex() == -1 ||
                departmentID.getSelectedItem() == null;
    }

    public Staff getStaff()
    {
        return new Staff(id.getText(), name.getText(), sex.getSelectedItem().toString(), idCard.getText(), dob.getDateModel().getValue(),
                 homeTown.getText(), phone.getText(), facilityID.getSelectedItem().toString(),
                role.getSelectedItem().toString(), departmentID.getSelectedItem().toString());
    }

}
