package administrator.gui.table;

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

    public ButtonCell(String title, String name, Consumer<String> onClick)
    {
        super(title);
        addActionListener(e -> onClick.accept(name));
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
