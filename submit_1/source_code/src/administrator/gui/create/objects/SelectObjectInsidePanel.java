package administrator.gui.create.objects;

import common.gui.ImageRepository;
import common.gui.ButtonImage;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * administrator.gui.create
 * Created by NhatLinh - 19127652
 * Date 3/26/2022 - 2:13 PM
 * Description: ...
 */
public class SelectObjectInsidePanel extends JPanel {

    private final DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

    public SelectObjectInsidePanel(Supplier<Collection<String>> getObjs, Consumer<Component> onDel)
    {
        super(new BorderLayout());
        ButtonImage delButton = new ButtonImage("delete.png", ImageRepository.SMALL, "Delete");
        delButton.addActionListener(e -> onDel.accept(this));
        JComboBox<String> insideObjs = new JComboBox<>(model);
        insideObjs.setPreferredSize(new Dimension(150, insideObjs.getPreferredSize().height));
        AutoCompleteDecorator.decorate(insideObjs);
        insideObjs.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                model.removeAllElements();
                Collection<String> objs = getObjs.get();
                if (objs != null && !objs.isEmpty())
                    model.addAll(objs);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        add(delButton, BorderLayout.LINE_START);
        add(insideObjs, BorderLayout.CENTER);
    }

    public String getSelectedItem() {
        Object select = model.getSelectedItem();
        if (select == null)
            return null;
        return (String)select;
    }
}
