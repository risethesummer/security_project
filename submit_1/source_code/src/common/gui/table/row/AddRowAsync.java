package common.gui.table.row;

import javax.swing.*;

/**
 * common.gui.table.row
 * Created by NhatLinh - 19127652
 * Date 3/26/2022 - 11:03 PM
 * Description: ...
 */
public class AddRowAsync extends Thread {
    private final JPanel parent;
    private final JPanel child;

    public AddRowAsync(JPanel parent, JPanel child)
    {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public void run() {
        try
        {
            SwingUtilities.invokeLater(() -> {
                parent.add(child, parent.getComponentCount() - 1);
                parent.updateUI();
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
