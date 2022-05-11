package users.patient;
import common.gui.table.cells.DatePickerCell;
import main.MainFrame;
import users.common.TwoHorizontalComponentSection;
import users.dao.Patient;
import users.dao.Staff;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.util.Date;
import java.util.function.Predicate;

/**
 * users.patient
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 1:12 PM
 * Description: ...
 */
public class PatientInformationPanel extends JPanel {

    private final JTextField id = new JTextField("");
    private final JTextField facilityID = new JTextField("");

    private final JTextField oldName = new JTextField("");
    private final JTextField newName = new JTextField("");

    private final JTextField oldIdCard = new JTextField("");
    private final JTextField newIdCard = new JTextField("");

    private final JTextField oldDob = new JTextField("");
    private final DatePickerCell newDob = new DatePickerCell(false);

    private final JTextField oldNumber = new JTextField("");
    private final JTextField newNumber = new JTextField("");

    private final JTextField oldStreet = new JTextField("");
    private final JTextField newStreet = new JTextField("");

    private final JTextField oldDistrict = new JTextField("");
    private final JTextField newDistrict = new JTextField("");

    private final JTextField oldCity = new JTextField("");
    private final JTextField newCity = new JTextField("");

    private final JTextField anamnesis = new JTextField("");
    private final JTextField familyAnamnesis = new JTextField("");
    private final JTextField allergyDrugs = new JTextField("");

    public PatientInformationPanel(Predicate<Patient> update)
    {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        MainFrame.addComponentAndDisable(this, id, "Patient ID");

        TwoHorizontalComponentSection nameSection = new TwoHorizontalComponentSection(
                oldName, "Current name", newName, "New name"
        );
        MainFrame.addComponent(this, nameSection, "Patient name");

        MainFrame.addComponentAndDisable(this, facilityID, "Facility ID");

        TwoHorizontalComponentSection idCardSection = new TwoHorizontalComponentSection(
                oldIdCard, "Current identity card", newIdCard, "New identity card"
        );
        MainFrame.addComponent(this, idCardSection, "Identity card");

        TwoHorizontalComponentSection dobSection = new TwoHorizontalComponentSection(
                oldDob, "Current date of birth", (JComponent)newDob.getComponent(), "New date of birth"
        );
        MainFrame.addComponent(this, dobSection, "Day of birth");

        TwoHorizontalComponentSection numberSection = new TwoHorizontalComponentSection(
                oldNumber, "Current number", newNumber, "New number"
        );
        MainFrame.addComponent(this, numberSection, "House number");

        TwoHorizontalComponentSection streetSection = new TwoHorizontalComponentSection(
                oldStreet, "Current street", newStreet, "New street"
        );
        MainFrame.addComponent(this, streetSection, "Street");

        TwoHorizontalComponentSection districtSection = new TwoHorizontalComponentSection(
                oldDistrict, "Current district", newDistrict, "New district"
        );
        MainFrame.addComponent(this, districtSection, "District");

        TwoHorizontalComponentSection citySection = new TwoHorizontalComponentSection(
                oldCity, "Current street", newCity, "New street"
        );
        MainFrame.addComponent(this, citySection, "City");

        MainFrame.addComponentAndDisable(this, anamnesis, "Tiền sử bệnh");
        MainFrame.addComponentAndDisable(this, familyAnamnesis, "Tiền sử bệnh gia đình");
        MainFrame.addComponentAndDisable(this, allergyDrugs, "Dị ứng thuốc");

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

    public Patient getUpdate()
    {
        String name = newName.getText().isBlank() ? oldName.getText() : newName.getText();
        java.util.Date dob = !newDob.getDateModel().getValue().before(java.sql.Date.from(Instant.now())) ? java.sql.Date.valueOf(oldDob.getText()) : newDob.getDateModel().getValue();
        String idCard = newIdCard.getText().isBlank() ? oldIdCard.getText() : newIdCard.getText();
        String number = newNumber.getText().isBlank() ? oldNumber.getText() : newNumber.getText();
        String street = newStreet.getText().isBlank() ? oldStreet.getText() : newStreet.getText();
        String district = newDistrict.getText().isBlank() ? oldDistrict.getText() : newDistrict.getText();
        String city = newCity.getText().isBlank() ? oldCity.getText() : newCity.getText();

        return new Patient(id.getText(), name, null, idCard, dob, number, street, district, city, null, null, null);
    }

    public void setPatient(Patient patient)
    {
        id.setText(patient.id());
        facilityID.setText(patient.facilityID());

        oldName.setText(patient.name());
        newName.setText("");

        oldIdCard.setText(patient.idCard());
        newIdCard.setText("");

        oldDob.setText(patient.dob().toString());
        newDob.getDateModel().setValue(Date.from(Instant.now()));

        oldNumber.setText(patient.number());
        newNumber.setText("");

        oldStreet.setText(patient.street());
        newStreet.setText("");

        oldDistrict.setText(patient.district());
        newDistrict.setText("");

        oldCity.setText(patient.city());
        newCity.setText("");

        anamnesis.setText(patient.anamnesis());
        familyAnamnesis.setText(patient.familyAnamnesis());
        allergyDrugs.setText(patient.allergyDrugs());
    }
}
