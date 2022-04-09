package administrator.dbHandler;

import common.handler.DBHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * administrator.dbHandler
 * Created by NhatLinh - 19127652
 * Date 3/24/2022 - 1:30 PM
 * Description: ...
 */
public abstract class DBAHandler
{
    protected final String userName;
    protected final String password;

    public DBAHandler(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
    }

    public PreparedStatement getCallableStatement(String sql)
    {
        try {
            Connection conn = DBHandler.getInstance().getConnection(userName, password);
            return conn.prepareCall(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PreparedStatement getPreparedStatement(String sql)
    {
        try {
            Connection conn = DBHandler.getInstance().getConnection(userName, password);
            return conn.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
