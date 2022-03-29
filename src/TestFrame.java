import common.gui.UserPanel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import java.awt.*;

/**
 * PACKAGE_NAME
 * Created by NhatLinh - 19127652
 * Date 3/26/2022 - 1:29 PM
 * Description: ...
 */
public class TestFrame extends JFrame {

    public TestFrame(Component component)
    {
        getContentPane().add(component);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        pack();
    }
}
