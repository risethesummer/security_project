package administrator.dbHandler.roleAndUser;

import administrator.dao.DBObject;
import administrator.dao.role.Role;
import administrator.dao.user.User;
import administrator.dao.user.UserFull;
import administrator.dbHandler.DBAHandler;
import administrator.dbHandler.IDBAHandler;
import common.handler.DBHandler;
import oracle.jdbc.OracleConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * administrator.dbHandler
 * Created by NhatLinh - 19127652
 * Date 3/24/2022 - 10:34 PM
 * Description: ...
 */
public class DBAUserHandler extends DBAHandler  implements IDBAHandler
{
    public DBAUserHandler(String userName, String password) {
        super(userName, password);
    }

    @Override
    public List<DBObject> getObjects() {

        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement("SELECT USERNAME FROM DBA_USERS ORDER BY USERNAME");
                 PreparedStatement roleStatement = conn.prepareStatement("SELECT GRANTEE, GRANTED_ROLE FROM DBA_ROLE_PRIVS");
                 ResultSet resultSet = statement.executeQuery();
                 ResultSet roleSet = roleStatement.executeQuery())
            {
                HashMap<String, String> roleMap = new HashMap<>(); //User -> role
                String role;
                String user;
                while (roleSet.next())
                {
                    user = roleSet.getString(1);
                    role = roleSet.getString(2);
                    roleMap.putIfAbsent(user, role);
                }
                List<DBObject> result = new ArrayList<>();
                while (resultSet.next())
                {
                    user = resultSet.getString(1);
                    role = roleMap.get(user);
                    final String finalRole = role;
                    result.add(new User(user,
                                    role == null ?
                                    null :
                                    new ArrayList<>(){
                                        {
                                            add(finalRole);
                                        }}));
                }
                return result;
            }
        } catch (SQLException s) {
            return null;
        }
    }

    @Override
    public boolean createObject(DBObject object) {

        try
        {
            UserFull user = (UserFull)object;
            try (OracleConnection conn = (OracleConnection)DBHandler.getInstance().getConnection(userName, password);
                 CallableStatement statement = conn.prepareCall("{CALL sys.create_user(?, ?, ?, ?, ?)}"))
            {
                Array arrIn = conn.createOracleArray("ARRAY_TABLE", user.getRoles().toArray());
                statement.registerOutParameter(1, Types.INTEGER);
                statement.setString(2, user.getName());
                statement.setString(3, user.getPassword());
                statement.setArray(4, arrIn);
                statement.setInt(5, user.isCommonUser() ? 0 : 1);
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

    @Override
    public boolean checkNameExists(String name) {
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement("SELECT COUNT (username) FROM dba_users WHERE username = UPPER(?)"))
            {
                statement.setString(1, name);
                try (ResultSet res = statement.executeQuery())
                {
                    res.next();
                    return res.getInt(1) == 1;
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
                 CallableStatement statement = conn.prepareCall("{CALL SYS.DROP_USER(?, ?)"))
            {
                statement.setString(1, name);
                statement.setInt(2, DBObject.isCommonObj(name) ? 0 : 1);
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
                 PreparedStatement statement = conn.prepareStatement("SELECT GRANTED_ROLE FROM DBA_ROLE_PRIVS WHERE GRANTEE = ? ORDER BY GRANTED_ROLE"))
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
    public Collection<DBObject> getInsideObjectsToGrant(String name) {
        return getInsideObjects(name);
    }

    @Override
    public Collection<DBObject> getOutsideObjects(String name) {
            try
            {
                try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                    PreparedStatement statement = conn.prepareStatement(
                            "SELECT dr.ROLE " +
                            "FROM DBA_ROLES dr " +
                            "WHERE " +
                            "    NOT EXISTS (SELECT drp.GRANTED_ROLE " +
                            "                FROM DBA_ROLE_PRIVS drp " +
                            "                WHERE drp.GRANTED_ROLE = dr.ROLE AND drp.GRANTEE = ?)"))
                {
                    statement.setString(1, name);
                    try (ResultSet resultSet = statement.executeQuery())
                    {
                        List<DBObject> objs = new ArrayList<>();
                        while (resultSet.next())
                            objs.add(new Role(resultSet.getString(1)));
                        return objs;
                    }
                }
        }
        catch (Exception e )
        {
            e.printStackTrace();
            return null;
        }
    }
}
