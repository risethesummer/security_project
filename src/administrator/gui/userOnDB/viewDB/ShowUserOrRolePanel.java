package administrator.gui.userOnDB.viewDB;

import javax.swing.*;
import java.util.Hashtable;

/**
 * administrator.gui.userOnDB.viewDB
 * Created by NhatLinh - 19127652
 * Date 3/14/2022 - 3:52 PM
 * Description: ...
 */
public class ShowUserOrRolePanel extends SearchingDBObjectPanel {
    private final Hashtable<String, JComponent> compManager = new Hashtable<>();

    public ShowUserOrRolePanel(String title) {
        super();
        setBorder(BorderFactory.createTitledBorder(title));
    }


    public void remove(String compName)
    {
        JComponent comp = compManager.remove(compName);
        if (comp != null)
            contentSection.remove(comp);
    }

    public void add(String comp, JButton button)
    {
        contentSection.add(button);
        compManager.put(comp, button);
    }
}
