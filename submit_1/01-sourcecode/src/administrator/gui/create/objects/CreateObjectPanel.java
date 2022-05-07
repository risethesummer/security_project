package administrator.gui.create.objects;

import administrator.dao.DBObject;
import administrator.dbHandler.IDBAHandler;
import common.gui.ImageRepository;
import common.gui.table.row.AddRowAsync;
import common.gui.ButtonImage;
import common.gui.UserPanel;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

    protected final JCheckBox commonBox = new JCheckBox();
    protected final JTextField confirmUserName = new JTextField();
    protected final JPanel commonSection = new JPanel(new BorderLayout());

    public CreateObjectPanel(IDBAHandler handler, IDBAHandler insideHandler)
    {
        super();
        this.handler = handler;
        this.insideHandler = insideHandler;

        commonBox.setSelected(true);
        commonBox.addItemListener(e -> {
            String username = getUsername();
            if (commonBox.isSelected())
                username = "C##" + username;
            confirmUserName.setText(username);
        });
        confirmUserName.setEditable(false);
        commonSection.add(commonBox, BorderLayout.LINE_START);
        commonSection.add(confirmUserName, BorderLayout.CENTER);
        commonSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, commonSection.getPreferredSize().height));
        userNameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            private void update()
            {
                String username = getUsername();
                if (commonBox.isSelected())
                    username = "C##" + username;
                confirmUserName.setText(username);
            }
        });

        ButtonImage addButton = new ButtonImage("add.png", ImageRepository.SMALL, "Create new dependency");
        addButton.addActionListener(e -> {
            SelectObjectInsidePanel select = new SelectObjectInsidePanel(this::getAvailable, insideObjsPanel::remove);
            AddRowAsync async = new AddRowAsync(insideObjsPanel, select);
            async.start();
            try {
                async.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        insideObjsPanel.add(addButton);
        add(commonSection, 4);
        add(insideObjsPanel, 5);
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
