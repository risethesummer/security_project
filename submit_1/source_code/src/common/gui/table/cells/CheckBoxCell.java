package common.gui.table.cells;
import javax.swing.*;
import java.awt.*;

/**
 * common.gui.table.cells
 * Created by NhatLinh - 19127652
 * Date 3/14/2022 - 4:02 PM
 * Description: ...
 */
public class CheckBoxCell extends JCheckBox implements ICell {

    public CheckBoxCell(boolean initial)
    {
        super();
        setSelected(initial);
    }
    @Override
    public Component getComponent() {
        return this;
    }
}
