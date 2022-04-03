package administrator.gui.table.cells;

import administrator.gui.ImageRepository;
import administrator.gui.table.row.IRow;
import common.gui.ButtonImage;

import javax.swing.*;
import java.awt.*;

/**
 * administrator.gui.table.cells
 * Created by NhatLinh - 19127652
 * Date 3/29/2022 - 1:08 PM
 * Description: ...
 */
public class UpDownButtonCell extends JPanel implements ICell  {

    public UpDownButtonCell(Runnable up, Runnable down)
    {
        super(new GridLayout(2, 1));
        ButtonImage upButton = new ButtonImage("up.png", ImageRepository.SMALL, "Move up");
        upButton.addActionListener(e -> {
            up.run();
        });
        ButtonImage downButton = new ButtonImage("down.png", ImageRepository.SMALL, "Move down");
        downButton.addActionListener(e -> {
            down.run();
        });
        add(upButton);
        add(downButton);
    }

    @Override
    public Component getComponent() {
        return this;
    }
}