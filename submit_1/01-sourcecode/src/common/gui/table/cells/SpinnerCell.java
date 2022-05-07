package common.gui.table.cells;

import javax.swing.*;
import java.awt.*;

/**
 * common.gui.table.cells
 * Created by NhatLinh - 19127652
 * Date 3/26/2022 - 11:22 PM
 * Description: ...
 */
public class SpinnerCell extends JSpinner implements ICell {
    public SpinnerCell(int min, int max, int val)
    {
        setModel(new SpinnerNumberModel(val, min, max, 1));
        JFormattedTextField spinnerField = ((JSpinner.DefaultEditor)this.getEditor()).getTextField();
        //spinnerField.setEditable(false);
        spinnerField.setHorizontalAlignment(JTextField.LEFT);
    }

    public SpinnerCell(int min, int max)
    {
        this(min, max, min);
    }

    @Override
    public SpinnerNumberModel getModel() {
        return (SpinnerNumberModel) super.getModel();
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
