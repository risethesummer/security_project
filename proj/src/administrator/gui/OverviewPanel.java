package administrator.gui;

import administrator.dao.DBObject;
import administrator.gui.table.TablePanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.function.Function;

/**
 * administrator.gui
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 3:06 PM
 * Description: ...
 */
public abstract class OverviewPanel extends JPanel {
    protected final JButton createButton = new JButton();
    //protected final JButton reloadButton = new JButton("Reload information");
    protected final JTextField searchFiled = new JTextField();
    protected final TablePanel tablePanel;
    public OverviewPanel(String[] titles)
    {
        super(new BorderLayout());

        tablePanel = new TablePanel(titles);

        //JPanel searchSection = new JPanel(new GridLayout(1, 2));
        searchFiled.setToolTipText("Input to search");
        //searchSection.add(reloadButton);
        //searchSection.add(searchFiled);
        this.searchFiled.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                getHint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                getHint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                getHint();
            }
        });

        JPanel inputSection = new JPanel(new GridLayout(2, 1));
        inputSection.add(createButton);
        inputSection.add(searchFiled);

        add(inputSection, BorderLayout.PAGE_START);
        add(tablePanel, BorderLayout.CENTER);
    }

    protected abstract void getHint();
}
