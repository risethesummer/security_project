package administrator.gui.table.cells;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * administrator.gui
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 1:26 PM
 * Description: ...
 */
public class ButtonCell extends JButton implements ICell {

    public ButtonCell(String title)
    {
        super(title);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public ButtonCell(String title, Runnable onClick)
    {
        this(title);
        addActionListener(e -> onClick.run());
    }

    public ButtonCell(String title, Consumer<Component> onClick)
    {
        this(title);
        addActionListener(e -> onClick.accept(this));
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
