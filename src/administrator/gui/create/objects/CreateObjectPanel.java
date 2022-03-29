package administrator.gui.create.objects;

import administrator.dao.DBObject;
import administrator.dbHandler.IDBAHandler;
import administrator.gui.ImageRepository;
import administrator.gui.table.row.AddRowAsync;
import common.gui.ButtonImage;
import common.gui.UserPanel;
import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashSet;

/**
 * administrator.gui.create
 * Created by NhatLinh - 19127652
 * Date 3/26/2022 - 1:46 PM
 * Description: ...
 */
public abstract class CreateObjectPanel extends UserPanel {

    protected final IDBAHandler handler;
    protected final IDBAHandler insideHandler;
    protected final JPanel insideObjsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    public CreateObjectPanel(IDBAHandler handler, IDBAHandler insideHandler)
    {
        super();
        this.handler = handler;
        this.insideHandler = insideHandler;
        ButtonImage addButton = new ButtonImage("add.png", ImageRepository.SMALL, "Create new dependency");
        addButton.addActionListener(e -> {
            SelectObjectInsidePanel select = new SelectObjectInsidePanel(this::getAvailable, insideObjsPanel::remove);
            AddRowAsync async = new AddRowAsync(insideObjsPanel, select);
            async.start();
        });
        insideObjsPanel.add(addButton);
        add(insideObjsPanel, 4);
    }

    public Collection<String> getAvailable()
    {
        try
        {
            HashSet<String> currentObjs = new HashSet<>();
            for (DBObject obj : insideHandler.getObjects())
                currentObjs.add(obj.getName());
            HashSet<String> choices = getChoices();
            currentObjs.removeAll(choices);
            return currentObjs;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public HashSet<String> getChoices()
    {
        HashSet<String> choices = new HashSet<>();
        for (Component c : insideObjsPanel.getComponents())
        {
            try
            {
                String select = ((SelectObjectInsidePanel)c).getSelectedItem();
                if (select != null)
                    choices.add(select);
            }
            catch (Exception e) {}
        }
        return choices;
    }
}
