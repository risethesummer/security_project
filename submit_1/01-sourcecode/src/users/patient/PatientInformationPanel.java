package users.patient;
import main.MainFrame;
import users.dao.Patient;

import javax.swing.*;
import java.awt.*;

/**
 * users.patient
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 1:12 PM
 * Description: ...
 */
public class PatientInformationPanel extends JPanel {
    private final JTextField id = new JTextField("");
    private final JTextField facilityID = new JTextField("");
    private final JTextField name = new JTextField("");
    private final JTextField idCard = new JTextField("");
    private final JTextField dob = new JTextField("");
    private final JTextField number = new JTextField("");
    private final JTextField street = new JTextField("");
    private final JTextField district = new JTextField("");
    private final JTextField city = new JTextField("");
    private final JTextField anamnesis = new JTextField("");
    private final JTextField familyAnamnesis = new JTextField("");
    private final JTextField allergyDrugs = new JTextField("");

    public PatientInformationPanel()
    {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        MainFrame.addComponentAndDisable(this, id, "Mã bệnh nhân");
        MainFrame.addComponentAndDisable(this, facilityID, "Mã cơ sở y tế");
        MainFrame.addComponentAndDisable(this, name, "Họ tên");
        MainFrame.addComponentAndDisable(this, idCard, "CMND");
        MainFrame.addComponentAndDisable(this, dob, "Ngày sinh");
        MainFrame.addComponentAndDisable(this, number, "Số nhà");
        MainFrame.addComponentAndDisable(this, street, "Tên đường");
        MainFrame.addComponentAndDisable(this, district, "Quận/huyện");
        MainFrame.addComponentAndDisable(this, city, "Tỉnh/Thành phố");
        MainFrame.addComponentAndDisable(this, anamnesis, "Tiền sử bệnh");
        MainFrame.addComponentAndDisable(this, familyAnamnesis, "Tiền sử bệnh gia đình");
        MainFrame.addComponentAndDisable(this, allergyDrugs, "Dị ứng thuốc");
    }

    public void setPatient(Patient patient)
    {
        id.setText(patient.id());
        facilityID.setText(patient.facilityID());
        name.setText(patient.name());
        idCard.setText(patient.idCard());
        dob.setText(patient.dob().toString());
        number.setText(patient.number());
        street.setText(patient.street());
        district.setText(patient.district());
        city.setText(patient.city());
        anamnesis.setText(patient.anamnesis());
        familyAnamnesis.setText(patient.familyAnamnesis());
        allergyDrugs.setText(patient.allergyDrugs());
    }
}
