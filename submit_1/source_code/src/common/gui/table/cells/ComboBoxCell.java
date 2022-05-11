package common.gui.table.cells;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * common.gui.table.cells
 * Created by NhatLinh - 19127652
 * Date 3/25/2022 - 12:49 AM
 * Description: ...
 */
public class ComboBoxCell extends JComboBox<String> implements ICell {
    public ComboBoxCell()
    {
        super(new DefaultComboBoxModel<>());
    }

    public ComboBoxCell(Collection<String> items)
    {
        this();
        if (items != null)
            getModel().addAll(items);
    }

    public ComboBoxCell(String[] items)
    {
        this(Arrays.stream(items).toList());
    }

    @Override
    public DefaultComboBoxModel<String> getModel() {
        return (DefaultComboBoxModel<String>)super.getModel();
    }

    @Override
    public Component getComponent() {
        return this;
    }

    public void setDropEvent(Supplier<Collection<String>> getItems)
    {
        addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                Collection<String> ids = getItems.get();
                DefaultComboBoxModel<String> model = getModel();
                SwingUtilities.invokeLater(() -> {
                    model.removeAllElements();
                    if (ids != null && !ids.isEmpty())
                        model.addAll(ids);
                });
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
    }
}
