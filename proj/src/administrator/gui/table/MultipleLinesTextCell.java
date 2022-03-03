package administrator.gui.table;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * administrator.gui.table
 * Created by NhatLinh - 19127652
 * Date 3/1/2022 - 10:00 PM
 * Description: ...
 */
public class MultipleLinesTextCell extends JTextArea implements ICell {

    public MultipleLinesTextCell(String text)
    {
        super(text);
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
