package administrator.gui.create.table;

import administrator.dao.table.property.Property;
import administrator.dao.table.property.PropertyFactory;
import administrator.dao.table.property.References;
import administrator.dbHandler.table.ITableHandler;
import administrator.gui.table.cells.*;
import administrator.gui.table.row.NColumnsPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * administrator.gui.table.row
 * Created by NhatLinh - 19127652
 * Date 3/26/2022 - 11:09 PM
 * Description: ...
 */
public class DBTablePropertyPanel extends NColumnsPanel {

    public TextCell getNameCell() {
        return nameCell;
    }

    private final TextCell nameCell = new TextCell("");
    private final ComboBoxCell typeCell = new ComboBoxCell(new String[] {
            "CHAR", "VARCHAR2", "NCHAR",
            "NVARCHAR2", "NUMBER", "DATE", "RAW"
    });
    private final SpinnerCell lengthCell = new SpinnerCell(1, 100);
    private final SpinnerCell precisionCell = new SpinnerCell(-84, 127, 0);
    private final CheckBoxCell pkCell = new CheckBoxCell(false);
    private final CheckBoxCell uniqueCell = new CheckBoxCell(false);
    private final CheckBoxCell fkCell = new CheckBoxCell(false);
    private final CheckBoxCell nullableCell = new CheckBoxCell(true);
    private final ComboBoxCell referencesCell = new ComboBoxCell();
    private final Predicate<String> checkNameExists;

    private static final HashMap<String, Integer> lengths = new HashMap<>(){
        {
            put("CHAR", 2000);
            put("VARCHAR2", 4000);
            put("NCHAR", 2000);
            put("NVARCHAR2", 4000);
            put("NUMBER", 38);
            put("RAW", 2000);
        }
    };

    public DBTablePropertyPanel(ITableHandler handler, Predicate<String> checkNameExists, Consumer<Component> onDel, BiConsumer<Component, Boolean> onMove) {
        super(10);
        this.checkNameExists = checkNameExists;
        nameCell.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                check();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                check();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                check();
            }

            private void check()
            {
                try
                {
                    if (checkName())
                        SwingUtilities.invokeLater(() -> nameCell.setBorder(BorderFactory.createLineBorder(Color.GRAY)));
                    else
                        SwingUtilities.invokeLater(() -> nameCell.setBorder(BorderFactory.createLineBorder(Color.RED)));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        typeCell.addItemListener(e -> {
            if (typeCell.getSelectedItem() != null)
            {
                String select = (String) typeCell.getSelectedItem();
                Integer len = lengths.get(select);
                if (len != null)
                {
                    SpinnerNumberModel model = lengthCell.getModel();
                    SwingUtilities.invokeLater(() -> {
                        model.setValue(1);
                        model.setMaximum(len);
                    });
                }
            }
        });

        pkCell.addActionListener(e -> {
            if (pkCell.isSelected())
            {
                SwingUtilities.invokeLater(() -> {
                    nullableCell.setSelected(false);
                    uniqueCell.setSelected(false);
                });
            }
        });

        //PK can not be unique again
        uniqueCell.addActionListener(e -> {
            if (uniqueCell.isSelected() && pkCell.isSelected())
                SwingUtilities.invokeLater(() -> uniqueCell.setSelected(false));
        });

        //PK can not be null
        nullableCell.addActionListener(e -> {
            if (nullableCell.isSelected() && pkCell.isSelected())
                SwingUtilities.invokeLater(() -> nullableCell.setSelected(false));
        });

        fkCell.addActionListener(e -> {
            if (!fkCell.isSelected())
                SwingUtilities.invokeLater(referencesCell.getModel()::removeAllElements);
        });

        referencesCell.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(referencesCell.getModel()::removeAllElements);
                if (fkCell.isSelected())
                {
                    java.util.List<References> referencesList = handler.getReferences(nameCell.getName(), (int)lengthCell.getModel().getValue());
                    if (referencesList == null || referencesList.isEmpty())
                        return;
                    Collection<String> tables = referencesList.stream().map(References::toString).toList();
                    if (tables != null)
                        SwingUtilities.invokeLater(() -> referencesCell.getModel().addAll(tables));
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (fkCell.isSelected() && referencesCell.getModel().getSelectedItem() == null)
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(referencesCell, "The foreign key must have its reference column");
                        referencesCell.setBorder(BorderFactory.createLineBorder(Color.RED));
                    });
                else
                    SwingUtilities.invokeLater(() -> referencesCell.setBorder(BorderFactory.createLineBorder(Color.GRAY)));
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
        ButtonCell delButton = new ButtonCell("Delete");
        delButton.addActionListener(e -> onDel.accept(this));

        UpDownButtonCell moveCell = new UpDownButtonCell(() -> onMove.accept(this, true), () -> onMove.accept(this, false));
        addCell(nameCell, typeCell, lengthCell, precisionCell, pkCell, uniqueCell, fkCell, nullableCell, referencesCell, delButton, moveCell);
    }

    public boolean checkValid()
    {
        return !checkNameNotValid()
                && !checkFKNotValid()
                && !checkUniqueNotValid()
                && !checkNullableNotValid()
                && !checkLengthNotValid();
    }

    private boolean checkNameNotValid()
    {
        String name = nameCell.getText();
        return name.isBlank() || name.contains(" ") || !Character.isAlphabetic(name.charAt(0)) || checkNameExists.test(name);
    }

    private boolean checkFKNotValid()
    {
        return fkCell.isSelected() && referencesCell.getModel().getSelectedItem() == null;
    }

    private boolean checkUniqueNotValid()
    {
        return  pkCell.isSelected() && uniqueCell.isSelected();
    }

    private boolean checkNullableNotValid()
    {
        return  pkCell.isSelected() && nullableCell.isSelected();
    }

    private boolean checkLengthNotValid()
    {
        Integer maxLen = lengths.get(typeCell.getModel().getSelectedItem());
        if (maxLen == null)
            return true;
        return (int)lengthCell.getModel().getNumber() > maxLen;
    }

    public boolean checkName()
    {
        String name = nameCell.getText();
        if (name.isBlank())
        {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(nameCell, "Name may not be blank!"));
            return false;
        }
        if (name.contains(" "))
        {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(nameCell, "Name may not have space character!"));
            return false;
        }
        if (!Character.isAlphabetic(name.charAt(0)))
        {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(nameCell, "Name must start with an alphabetic character!"));
            return false;
        }
        if (checkNameExists.test(name))
        {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(nameCell, "Name has already existed!"));
            return false;
        }
        return true;
    }

    public Property getProperty()
    {
        Object refInput = referencesCell.getModel().getSelectedItem();
        References ref = refInput == null? null : References.fromString((String)refInput);
        if (fkCell.isSelected())
            return PropertyFactory.generateProperty(nameCell.getText(),
                    (String)typeCell.getModel().getSelectedItem(),
                    (int)lengthCell.getModel().getNumber(),
                    (int)precisionCell.getModel().getNumber(),
                    pkCell.isSelected(),
                    uniqueCell.isSelected(),
                    nullableCell.isSelected(),
                    ref);
        return PropertyFactory.generateProperty(nameCell.getText(),
                (String)typeCell.getModel().getSelectedItem(),
                (int)lengthCell.getModel().getNumber(),
                (int)precisionCell.getModel().getNumber(),
                pkCell.isSelected(),
                uniqueCell.isSelected(),
                nullableCell.isSelected());
    }


    @Override
    public String getHeader() {
        return nameCell.getName();
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
