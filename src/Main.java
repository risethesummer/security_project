import administrator.dao.table.property.References;
import administrator.dbHandler.roleAndUser.DBARoleHandler;
import administrator.dbHandler.roleAndUser.DBAUserHandler;
import administrator.dbHandler.table.TableHandler;
import administrator.gui.AdminFrame;
import administrator.gui.create.table.CreateTablePanel;
import dbHandler.DBHandler;

import javax.swing.*;
import javax.swing.plaf.ColorChooserUI;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * PACKAGE_NAME
 * Created by NhatLinh - 19127652
 * Date 2/16/2022 - 10:17 AM
 * Description: ...
 */
public class Main {


    public static void main(String[] args)
    {
        //System.out.println(References.fromString("Table(Column)").column());
        /*JPanel a  = new JPanel();
        JComponent t = new JComboBox<>();
        t.setBorder(BorderFactory.createLineBorder(Color.RED));
        JComponent t1 = new JComboBox<>();
        t1.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        a.add(t);
        a.add(t1);
        a.add(new JComboBox<>());*/
        //JButton test = new JButton("ASDasdasd");
        //test.setBackground(null);
        // TestFrame(test);
        if (DBHandler.getInstance().loginAsDBA("SYS AS SYSDBA", "linh1905"))
            new AdminFrame(new DBAUserHandler("SYS AS SYSDBA", "linh1905"), new DBARoleHandler("SYS AS SYSDBA", "linh1905"), new TableHandler("SYS AS SYSDBA", "linh1905"));
        else
            System.out.println("SHIT");
        //System.out.println(new JButton().getBackground().getClass());
        //System.out.println(test.getBackground());
        //IDBAHandler userHandler = new DBAUserHandler();
    }
}
