package administrator.gui;

import javax.swing.*;

/**
 * administrator.gui.privileges
 * Created by NhatLinh - 19127652
 * Date 3/31/2022 - 9:46 AM
 * Description: ...
 */
public interface INameCriticalPanel extends IComponent {
    void add(String name, JComponent component);
    void remove(String name);
    void clear();
    void updateUI();
}
