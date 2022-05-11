package users.common;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * users.healthFacility
 * Created by NhatLinh - 19127652
 * Date 5/7/2022 - 5:30 PM
 * Description: ...
 */

public class TwoHorizontalComponentSection extends JPanel {

    public TwoHorizontalComponentSection(JTextComponent first, String firstName,
                                         JComponent second, String secondName) {
        super(new GridLayout(1, 2));
        first.setEditable(false);
        first.setBorder(BorderFactory.createTitledBorder(firstName));
        second.setBorder(BorderFactory.createTitledBorder(secondName));
        add(first);
        add(second);
    }
}
