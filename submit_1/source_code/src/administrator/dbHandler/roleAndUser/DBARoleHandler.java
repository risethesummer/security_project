package administrator.dbHandler.roleAndUser;

import administrator.dao.DBObject;
import administrator.dao.role.Role;
import administrator.dao.user.User;
import administrator.dbHandler.DBAHandler;
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
 * Date 3/24/2022 - 10:33 PM
 * Description: ...
 */
public class DBARoleHandler extends DBAHandler  implements IRoleHandler {

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
                 CallableStatement statement = conn.prepareCall("{CALL SYS.DROP_ROLE(?, ?)"))
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
                 PreparedStatement statement = conn.prepareStatement(
                         "SELECT DISTINCT GRANTEE, 'ROLE' " +
                         "FROM DBA_ROLE_PRIVS drp " +
                         "WHERE drp.GRANTED_ROLE = ? AND EXISTS (SELECT r.ROLE " +
                         "                                              FROM DBA_ROLES r " +
                         "                                              WHERE r.ROLE = drp.GRANTEE) " +
                         "UNION " +
                         "SELECT DISTINCT GRANTEE, 'USER' " +
                         "FROM DBA_ROLE_PRIVS drp " +
                         "WHERE drp.GRANTED_ROLE = ? AND EXISTS (SELECT du.USERNAME " +
                         "                                              FROM DBA_USERS du " +
                         "                                              WHERE du.USERNAME = drp.GRANTEE)"))
            {
                statement.setString(1, name);
                statement.setString(2, name);
                try (ResultSet res = statement.executeQuery())
                {
                    List<DBObject> result = new ArrayList<>();
                    while (res.next()) {
                        if (res.getString(2).equals("ROLE"))
                            result.add(new Role(res.getString(1)));
                        else
                            result.add(new User(res.getString(1)));
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
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement(
                         "SELECT du.USERNAME " +
                                 "FROM DBA_USERS du " +
                                 "WHERE " +
                                 "    EXISTS (SELECT drp.GRANTEE " +
                                 "                FROM DBA_ROLE_PRIVS drp " +
                                 "                WHERE drp.GRANTEE = du.USERNAME AND drp.GRANTED_ROLE = ?)"))
            {
                statement.setString(1, name);
                try (ResultSet res = statement.executeQuery())
                {
                    List<DBObject> result = new ArrayList<>();
                    while (res.next())
                        result.add(new User(res.getString(1)));
                    return result;
                }

            }
        } catch (SQLException s) {
            return null;
        }
    }

    @Override
    public Collection<DBObject> getOutsideObjects(String name) {
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement(
                         "SELECT du.USERNAME " +
                                 "FROM DBA_USERS du " +
                                 "WHERE " +
                                 "    NOT EXISTS (SELECT drp.GRANTEE " +
                                 "                FROM DBA_ROLE_PRIVS drp " +
                                 "                WHERE drp.GRANTEE = du.USERNAME AND drp.GRANTED_ROLE = ?)")) {
                statement.setString(1, name);
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<DBObject> objs = new ArrayList<>();
                    while (resultSet.next())
                        objs.add(new User(resultSet.getString(1)));
                    return objs;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean createObject(DBObject object) {
        try
        {
            Role role = (Role) object;
            try (OracleConnection conn = (OracleConnection)DBHandler.getInstance().getConnection(userName, password);
                 CallableStatement statement = conn.prepareCall("{CALL sys.create_role(?, ?, ?, ?)}"))
            {
                Array arrIn = conn.createOracleArray("ARRAY_TABLE", role.getUsers().toArray());
                statement.registerOutParameter(1, Types.INTEGER);
                statement.setString(2, role.getName());
                statement.setArray(3, arrIn);
                statement.setInt(4, role.isCommonRole() ? 0 : 1);
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
    public Collection<String> getInsideRole(String name) {
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement(
                         "SELECT dr.ROLE " +
                                 "FROM DBA_ROLES dr " +
                                 "WHERE EXISTS (SELECT drp.GRANTEE " +
     "                                          FROM DBA_ROLE_PRIVS drp " +
     "                                          WHERE drp.GRANTEE = dr.ROLE AND drp.GRANTED_ROLE = ?)"))
            {
                statement.setString(1, name);
                try (ResultSet res = statement.executeQuery())
                {
                    List<String> result = new ArrayList<>();
                    while (res.next())
                        result.add(res.getString(1));
                    return result;
                }
            }
        }
        catch (SQLException s)
        {
            s.printStackTrace();
            return null;
        }
    }

    @Override
    public Collection<String> getOutsideRole(String name) {

        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement(
                         "SELECT dr.ROLE " +
                             "FROM DBA_ROLES dr " +
                             "WHERE " +
                             "    dr.ROLE != ? AND NOT EXISTS (SELECT drp.GRANTED_ROLE " +
                             "                FROM DBA_ROLE_PRIVS drp " +
                             "                WHERE drp.GRANTED_ROLE = ? AND drp.GRANTEE = dr.ROLE)"))
            {
                statement.setString(1, name);
                statement.setString(2, name);
                try (ResultSet res = statement.executeQuery())
                {
                    List<String> result = new ArrayList<>();
                    while (res.next())
                        result.add(res.getString(1));
                    return result;
                }
            }
        }
        catch (SQLException s)
        {
            s.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean grant(String role, String user) {
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement(
                         String.format("GRANT %s TO %s", role, user)))
            {
                statement.execute();
                return true;
            }
        }
        catch (SQLException s)
        {
            s.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean revoke(String role, String user) {
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement(
                         String.format("REVOKE %s FROM %s", role, user)))
            {
                statement.execute();
                return true;
            }
        }
        catch (SQLException s)
        {
            s.printStackTrace();
            return false;
        }
    }
}
