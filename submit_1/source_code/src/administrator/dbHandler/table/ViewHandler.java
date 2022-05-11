package administrator.dbHandler.table;

import administrator.dao.DBObject;
import administrator.dao.table.Table;
import administrator.dbHandler.DBAHandler;
import administrator.dbHandler.IDBAHandler;
import common.handler.DBHandler;

import javax.sound.midi.InvalidMidiDataException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * administrator.dbHandler.table
 * Created by NhatLinh - 19127652
 * Date 4/4/2022 - 9:51 PM
 * Description: ...
 */
public class ViewHandler extends DBAHandler implements IDBAHandler  {

    private final IDBAHandler tableHandler;
    public ViewHandler(String userName, String password, IDBAHandler tableHandler) {
        super(userName, password);
        this.tableHandler = tableHandler;
    }

    @Override
    public List<DBObject> getObjects() {
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement("SELECT OWNER, VIEW_NAME FROM DBA_VIEWS GROUP BY OWNER, VIEW_NAME");
                 ResultSet resultSet = statement.executeQuery())
            {
                List<DBObject> result = new ArrayList<>();
                while (resultSet.next())
                    result.add(new Table(resultSet.getString(1), resultSet.getString(2)));
                return result;
            }
        } catch (SQLException s) {
            return null;
        }
    }

    @Override
    public Collection<DBObject> getInsideObjects(String name) {
        return tableHandler.getInsideObjects(name);
    }

    @Override
    public Collection<DBObject> getInsideObjectsToGrant(String name) {
        return tableHandler.getInsideObjects(name);
    }

    @Override
    public boolean createObject(DBObject object) {
        return false;
    }

    @Override
    public boolean checkNameExists(String name) {
        return false;
    }

    @Override
    public boolean dropObject(String name) {
        try
        {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement(String.format("DROP VIEW %s", name)))
            {
                statement.execute();
                return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }



    @Override
    public Collection<DBObject> getOutsideObjects(String name) {
        return null;
    }
}
