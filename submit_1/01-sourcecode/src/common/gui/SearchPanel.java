package common.gui;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Arrays;

/**
 * common.gui
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 1:53 PM
 * Description: ...
 */
public abstract class SearchPanel extends JPanel {
    protected final JButton reloadBtn = new JButton("Reload");
    protected final JTextField inputField = new JTextField();
    //private final DefaultComboBoxModel<String> optionsModel = new DefaultComboBoxModel<>();
    protected final JComboBox<String> optionsCbb;
    private final JPanel searchAndReloadSection;

    public SearchPanel(String[] options)
    {
        super(new BorderLayout());
        reloadBtn.addActionListener(e -> reload());
        optionsCbb = new JComboBox<>(options);
        searchAndReloadSection = new JPanel(new GridLayout(2, 1));
        JPanel searchSection = new JPanel(new BorderLayout());
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                hint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                hint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                hint();
            }
        });
        searchSection.add(inputField, BorderLayout.CENTER);
        searchSection.add(optionsCbb, BorderLayout.LINE_END);
        searchAndReloadSection.add(reloadBtn);
        searchAndReloadSection.add(searchSection);
        add(searchAndReloadSection, BorderLayout.PAGE_START);
    }

    public void addOrigin(Component mainSection)
    {
        setMainSection(mainSection);
        add(searchAndReloadSection, BorderLayout.PAGE_START);
        updateUI();
    }

    public void setMainSection(Component mainSection)
    {
        removeAll();
        add(mainSection, BorderLayout.CENTER);
        updateUI();
    }

    protected abstract void reload();

    protected abstract void hint();
}
