package administrator.gui.table.cells;

import javax.print.attribute.standard.RequestingUserName;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * administrator.gui.table.cells
 * Created by NhatLinh - 19127652
 * Date 3/24/2022 - 12:38 PM
 * Description: ...
 */
public class LabelCell extends JLabel implements ICell {

    private final int maxLength = 25;

    public LabelCell(String title)
    {
        super(title);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public LabelCell(String title, Runnable onClick)
    {
        this(title);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
