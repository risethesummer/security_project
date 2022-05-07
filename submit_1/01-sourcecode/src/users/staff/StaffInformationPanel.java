package users.staff;

import main.MainFrame;
import users.dao.Patient;
import users.dao.Staff;

import javax.swing.*;

/**
 * users.staff
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 4:37 PM
 * Description: ...
 */
public class StaffInformationPanel extends JPanel {
    private final JTextField id = new JTextField("");
    private final JTextField name = new JTextField("");
    private final JTextField sex = new JTextField("");
    private final JTextField dob = new JTextField("");
    private final JTextField idCard = new JTextField("");
    private final JTextField homeTown = new JTextField("");
    private final JTextField phone = new JTextField("");
    private final JTextField facilityID = new JTextField("");
    private final JTextField role = new JTextField("");
    private final JTextField major = new JTextField("");

    public StaffInformationPanel()
    {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        MainFrame.addComponentAndDisable(this, id, "Patient ID");
        MainFrame.addComponentAndDisable(this, name, "Full name");
        MainFrame.addComponentAndDisable(this, sex, "Sex");
        MainFrame.addComponentAndDisable(this, dob, "Day of birth");
        MainFrame.addComponentAndDisable(this, idCard, "Identity card");
        MainFrame.addComponentAndDisable(this, homeTown, "Home town");
        MainFrame.addComponentAndDisable(this, phone, "Phone");
        MainFrame.addComponentAndDisable(this, facilityID, "Facility ID");
        MainFrame.addComponentAndDisable(this, role, "Role");
        MainFrame.addComponentAndDisable(this, major, "Department ID");
    }

    public void setStaff(Staff staff)
    {
        id.setText(staff.id());
        name.setText(staff.name());
        sex.setText(staff.sex());
        dob.setText(staff.dob().toString());
        idCard.setText(staff.idCard());
        homeTown.setText(staff.homeTown());
        phone.setText(staff.phone());
        facilityID.setText(staff.facilityID());
        role.setText(staff.role());
        major.setText(staff.major());
    }
}
