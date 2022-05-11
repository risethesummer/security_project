package administrator.gui.privileges.userRole;

import administrator.gui.INameCriticalPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;
import java.util.Map;

/**
 * administrator.gui.userOnDB.viewDB
 * Created by NhatLinh - 19127652
 * Date 3/14/2022 - 3:52 PM
 * Description: ...
 */
public class ShowDBObjectPanel extends SearchingDBObjectPanel implements INameCriticalPanel {
    private final Hashtable<String, Component> compManager = new Hashtable<>();

    public ShowDBObjectPanel(String title) {
        super();
        setBorder(BorderFactory.createTitledBorder(title));
    }

    @Override
    public void add(String name, JComponent component) {
        compManager.put(name, component);
        SwingUtilities.invokeLater(() -> contentSection.add(component));
    }

    public void remove(String compName)
    {
        Component comp = compManager.remove(compName);
        if (comp != null)
        {
            SwingUtilities.invokeLater(() -> {
                contentSection.remove(comp);
                contentSection.updateUI();
            });
        }
    }

    @Override
    public void clear() {
        SwingUtilities.invokeLater(contentSection::removeAll);
    }

    private void showAll()
    {
        for (Component c : compManager.values())
            SwingUtilities.invokeLater(() -> c.setVisible(true));
    }

    private void hide(String exception)
    {
        for (Map.Entry<String, Component> c : compManager.entrySet())
        {
            String header = c.getKey();
            if (header.length() > exception.length())
                header = header.substring(0, exception.length());
            if (header.equalsIgnoreCase(exception))
                SwingUtilities.invokeLater(() -> c.getValue().setVisible(true));
            else
                SwingUtilities.invokeLater(() -> c.getValue().setVisible(false));
        }
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    protected void getHint() {
        String search = inputField.getText();
        if (search.isBlank())
            showAll();
        else
            hide(search);
    }
}
