package common.handler;

import java.sql.*;

/**
 * administrator.dbHandler
 * Created by NhatLinh - 19127652
 * Date 3/24/2022 - 1:15 PM
 * Description: ...
 */
public class DBHandler {
    private static DBHandler instance = null;
    private static final String dbURL = "jdbc:oracle:thin:@localhost:11521:XE";

    private DBHandler()
    {
        try {
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DBHandler getInstance()
    {
        if (instance == null)
            instance = new DBHandler();
        return instance;
    }

    public Connection getConnection(String user, String password) throws SQLException {
        return DriverManager.getConnection
                (dbURL, user, password);
    }

    public String login(String userName, String password)
    {
        try
        {
            try (Connection conn = getConnection(userName, password);
                 CallableStatement statement = conn.prepareCall("{CALL SYS.LAY_VAI_TRO(?)}"))
            {
                statement.registerOutParameter(1, Types.NVARCHAR);
                statement.execute();
                Object role = statement.getObject(1);
                return role == null ? null : (String)role;
            }
        }
        catch (SQLException s)
        {
            s.printStackTrace();
            return null;
        }
    }

}
