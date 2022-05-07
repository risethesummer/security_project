package common.gui.table.cells;

import javax.swing.*;
import java.awt.*;

/**
 * administrator.gui
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 1:23 PM
 * Description: ...
 */
public class TextCell extends JTextField implements ICell {

    public TextCell(String title)
    {
        super(title);
    }

    public TextCell(String title, Runnable onClick)
    {
        super(title);
        addActionListener(e -> onClick.run());
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
