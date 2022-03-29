package administrator.dbHandler.roleAndUser;

import administrator.dao.DBObject;
import administrator.dao.role.Role;
import administrator.dao.user.User;
import administrator.dao.user.UserFull;
import administrator.dbHandler.DBAHandler;
import administrator.dbHandler.IDBAHandler;
import dbHandler.DBHandler;
import oracle.jdbc.OracleConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * administrator.dbHandler
 * Created by NhatLinh - 19127652
 * Date 3/24/2022 - 10:33 PM
 * Description: ...
 */
public class DBARoleHandler extends DBAHandler  implements IDBAHandler {

    public DBARoleHandler(String userName, String password) {
        super(userName, password);
    }

    @Override
    public List<DBObject> getObjects() {
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement("SELECT ROLE FROM DBA_ROLES ORDER BY ROLE");
                 PreparedStatement roleStatement = conn.prepareStatement("SELECT GRANTEE, GRANTED_ROLE FROM DBA_ROLE_PRIVS");
                 ResultSet resultSet = statement.executeQuery();
                 ResultSet roleSet = roleStatement.executeQuery())
            {
                HashMap<String, String> userMap = new HashMap<>(); //Role -> user
                String role;
                String user;
                while (roleSet.next())
                {
                    user = roleSet.getString(1);
                    role = roleSet.getString(2);
                    userMap.putIfAbsent(role, user);
                }
                List<DBObject> result = new ArrayList<>();
                while (resultSet.next())
                {
                    role = resultSet.getString(1);
                    user = userMap.get(role);
                    final String finalUser = user;
                    result.add(new User(role,
                            user == null ?
                                    null :
                                    new ArrayList<>(){
                                        {
                                            add(finalUser);
                                        }}));
                }
                return result;
            }
        }
        catch (SQLException s)
        {
            return null;
        }
    }

    @Override
    public boolean checkNameExists(String name) {
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement("SELECT COUNT(ROLE) FROM DBA_ROLES WHERE ROLE = UPPER(?)"))
            {
                statement.setString(1, name);
                try (ResultSet res = statement.executeQuery())
                {
                    res.next();
                    return res.getInt(1) != 1;
                }
            }
        } catch (SQLException s) {
            return false;
        }
    }

    @Override
    public boolean dropObject(String name) {
        try
        {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement("DROP ROLE " + name))
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
    public Collection<DBObject> getInsideObjects(String name) {
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement("SELECT GRANTEE FROM DBA_ROLE_PRIVS WHERE GRANTED_ROLE = ? ORDER BY GRANTED_ROLE"))
            {
                statement.setString(1, name);
                try (ResultSet res = statement.executeQuery())
                {
                    List<DBObject> result = new ArrayList<>();
                    while (res.next()) {
                        result.add(new Role(res.getString(1)));
                    }
                    return result;
                }

            }
        } catch (SQLException s) {
            return null;
        }
    }

    @Override
    public boolean createObject(DBObject object) {
        try
        {
            Role role = (Role) object;
            try (OracleConnection conn = (OracleConnection)DBHandler.getInstance().getConnection(userName, password);
                 CallableStatement statement = conn.prepareCall("{CALL create_role(?, ?, ?)}"))
            {
                Array arrIn = conn.createOracleArray("ARRAY_TABLE", role.getUsers().toArray());
                statement.registerOutParameter(1, Types.INTEGER);
                statement.setString(2, role.getName());
                statement.setArray(3, arrIn);
                statement.execute();
                return statement.getInt(1) == 1;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
