package administrator.gui.table.row;

import administrator.gui.IComponent;

import java.awt.*;

/**
 * administrator.gui.customTable
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 1:38 PM
 * Description: ...
 */
public interface IRow extends IComponent {
    String getHeader();
    static int compare(IRow r1, IRow r2)
    {
        return r1.getHeader().compareTo(r2.getHeader());
    }
}
