package administrator.gui.overview.viewDetails;

import administrator.dao.DBObject;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Vector;

/**
 * administrator.gui
 * Created by NhatLinh - 19127652
 * Date 3/25/2022 - 1:06 AM
 * Description: ...
 */
public class ViewInsideObjectsFrame extends JFrame implements IShowDetails {

    protected final JList<String> showObjects = new JList<>();

    public ViewInsideObjectsFrame(String title)
    {
        super(title);
        getContentPane().setLayout(new BorderLayout());
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        SwingUtilities.invokeLater(this::addComponent);
    }

    public void addComponent()
    {
        JScrollPane scrollPane = new JScrollPane(showObjects, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    public void setObjects(String name, Collection<DBObject> objs)
    {
        setTitle(name);
        Vector<String> data = new Vector<>();
        for (DBObject object : objs)
            data.add(object.getName());
        SwingUtilities.invokeLater(() ->
        {
            showObjects.setListData(data);
            setPreferredSize(showObjects.getPreferredSize());
        });
    }
}
