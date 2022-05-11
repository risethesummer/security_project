package administrator.dbHandler.table;

import administrator.dao.DBObject;
import administrator.dao.permissions.ActionPermission;
import administrator.dao.permissions.DetailedActionPermission;
import administrator.dao.permissions.GeneralPermission;
import administrator.dao.permissions.PermissionType;
import administrator.dao.table.Table;
import administrator.dao.table.property.*;
import administrator.dbHandler.DBAHandler;
import common.handler.DBHandler;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.util.*;

/**
 * administrator.dbHandler.table
 * Created by NhatLinh - 19127652
 * Date 3/27/2022 - 1:56 PM
 * Description: ...
 */
public class TableHandler extends DBAHandler implements ITableHandler {

    public TableHandler(String userName, String password) {
        super(userName, password);
    }

    @Override
    public List<DBObject> getObjects() {
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement("SELECT OWNER, TABLE_NAME FROM DBA_TABLES GROUP BY OWNER, TABLE_NAME");
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
    public boolean createObject(DBObject object) {
        try {
            Table table = (Table) object;
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement(table.getCreateSQL()))
            {
                System.out.println(table.getCreateSQL());
                statement.execute();
                return true;
            }
        }
        catch (SQLException s) {
            return false;
        }
    }

    @Override
    public boolean checkNameExists(String name) {
        try
        {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement("SELECT TABLE_NAME FROM DBA_TABLES WHERE OWNER = ? AND TABLE_NAME = ?"))
            {
                String[] parts = name.split("\\.");
                statement.setString(1, parts[0]);
                statement.setString(2, parts[1]);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean dropObject(String name) {
        try
        {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement(String.format("DROP TABLE %s CASCADE CONSTRAINTS", name)))
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
                 CallableStatement statement = conn.prepareCall("{CALL SYS.GET_TABLE_DETAILS(?, ?, ?)}"))
            {
                String[] parts = name.split("\\.");
                statement.setString(1, parts[0]);
                statement.setString(2, parts[1]);
                statement.registerOutParameter(3, OracleTypes.CURSOR);
                statement.execute();
                try (ResultSet resultSet = (ResultSet)statement.getObject(3))
                {
                    HashMap<String, DBObject> propertyMap = new HashMap<>();
                    while (resultSet.next())
                    {
                        try {
                            String pName = resultSet.getString(1);
                            String type = resultSet.getString(2);
                            int len = resultSet.getInt(3);
                            int precision = resultSet.getInt(4);
                            String cType = resultSet.getString(5);
                            boolean pk = false, fk = false, unique = false;
                            if (cType != null)
                            {
                                switch (cType) {
                                    case "P" -> pk = true;
                                    case "R" -> fk = true;
                                    case "U" -> unique = true;
                                }
                            }
                            boolean nullable = resultSet.getString(6).equals("Y");

                            DBObject storedObj = propertyMap.get(pName);
                            Property stored = storedObj == null ? null : (Property) storedObj;
                            if (fk) {
                                String tabRef = resultSet.getString(7);
                                String colRef = resultSet.getString(8);
                                if (stored != null) {
                                    stored.setFK(true);
                                    if (stored.getReferences() == null)
                                        stored.setReferences(new References(tabRef, colRef));
                                } else
                                    propertyMap.put(pName, PropertyFactory.generateProperty(pName, type, len, precision, false, false, nullable, new References(tabRef, colRef)));
                            } else {
                                if (stored != null) {
                                    if (unique)
                                        stored.setUnique(true);
                                    else if (pk)
                                        stored.setPK(true);
                                } else
                                    propertyMap.put(pName, PropertyFactory.generateProperty(pName, type, len, precision, pk, unique, nullable));
                            }
                        }
                        catch (Exception inE)
                        {
                            inE.printStackTrace();
                        }
                    }
                    return propertyMap.values();
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
        return null;
    }

    @Override
    public List<References> getTableReferences(String schemaIn, String dataType, int length) {
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 CallableStatement statement = conn.prepareCall("{CALL SYS.GET_SUITABLE_TABLE_REFFERRENCES(?, ?, ?, ?)}"))
            {
                statement.setString(1, schemaIn);
                statement.setString(2, dataType);
                statement.setInt(3, length);
                statement.registerOutParameter(4, OracleTypes.CURSOR);
                List<References> result = new ArrayList<>();
                statement.execute();
                try (ResultSet resultSet = (ResultSet)statement.getObject(4))
                {
                    String schema, table, column;
                    while (resultSet.next())
                    {
                        schema = resultSet.getString(1);
                        table = resultSet.getString(2);
                        column = resultSet.getString(3);
                        result.add(new References(schema, table, column));
                    }
                }
                return result;
            }
        } catch (SQLException s) {
            return null;
        }
    }

    @Override
    public List<String> getAllSchemas() {
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 CallableStatement statement = conn.prepareCall("SELECT DISTINCT OWNER FROM DBA_TABLES ORDER BY OWNER");
                ResultSet resultSet = statement.executeQuery())
            {
                List<String> result = new ArrayList<>();
                while (resultSet.next())
                    result.add(resultSet.getString(1));
                return result;
            }
        } catch (SQLException s) {
            return null;
        }
    }

    @Override
    public List<String> getTablesInSchema(String schema) {
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 CallableStatement statement = conn.prepareCall("SELECT TABLE_NAME FROM DBA_TABLES WHERE OWNER = ? ORDER BY TABLE_NAME"))
            {
                statement.setString(1, schema);
                List<String> result = new ArrayList<>();
                try (ResultSet resultSet = statement.executeQuery())
                {
                    while (resultSet.next())
                        result.add(resultSet.getString(1));
                }
                return result;
            }
        } catch (SQLException s) {
            return null;
        }
    }

    @Override
    public GeneralPermission getPrivileges(String user, String schema, String table) {
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 PreparedStatement statement = conn.prepareStatement("SELECT PRIVILEGE, GRANTABLE FROM DBA_TAB_PRIVS WHERE GRANTEE = ? AND OWNER = ? AND TABLE_NAME = ?");
                 PreparedStatement getColumnsStatement = conn.prepareStatement("SELECT COLUMN_NAME FROM DBA_TAB_COLUMNS WHERE OWNER = ? AND TABLE_NAME = ?")) {
                List<String> columns = new ArrayList<>();
                getColumnsStatement.setString(1, schema);
                getColumnsStatement.setString(2, table);
                try (ResultSet resultSet = getColumnsStatement.executeQuery()) {
                    while (resultSet.next())
                        columns.add(resultSet.getString(1));
                }


                Map<PermissionType, ActionPermission> permissionMap = new HashMap<>();
                statement.setString(1, user);
                statement.setString(2, schema);
                statement.setString(3, table);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        PermissionType privilege = PermissionType.valueOf(resultSet.getString(1));
                        boolean wgo = resultSet.getString(2).equals("YES");
                        switch (privilege) {
                            case INSERT, DELETE -> permissionMap.put(privilege, new ActionPermission(true, wgo));
                            case UPDATE, SELECT -> {
                                List<Boolean> columnChecks = columns.stream().map(e -> true).toList();
                                permissionMap.put(privilege, new DetailedActionPermission(true, wgo, columnChecks));
                            }
                        }
                    }
                }

                if (permissionMap.size() < 4) {
                    if (!permissionMap.containsKey(PermissionType.INSERT))
                        permissionMap.put(PermissionType.INSERT, new ActionPermission(false, false));
                    if (!permissionMap.containsKey(PermissionType.DELETE))
                        permissionMap.put(PermissionType.DELETE, new ActionPermission(false, false));
                    if (!permissionMap.containsKey(PermissionType.UPDATE)) {
                        try (PreparedStatement updateStatement = conn.prepareStatement("SELECT COLUMN_NAME, GRANTABLE FROM DBA_COL_PRIVS WHERE GRANTEE = ? AND OWNER = ? AND TABLE_NAME = ? AND PRIVILEGE = ?")) {
                            updateStatement.setString(1, user);
                            updateStatement.setString(2, schema);
                            updateStatement.setString(3, table);
                            updateStatement.setString(4, "UPDATE");

                            Map<String, Boolean> columnChecks = new HashMap<>(columns.size());
                            for (String col : columns)
                                columnChecks.put(col, false);

                            try (ResultSet resultSet = updateStatement.executeQuery()) {
                                if (!resultSet.isBeforeFirst())
                                    permissionMap.put(PermissionType.UPDATE, new DetailedActionPermission(false, false, columnChecks.values().stream().toList()));
                                else {
                                    boolean wgo = false;
                                    while (resultSet.next()) {
                                        String column = resultSet.getString(1);
                                        wgo = resultSet.getString(2).equals("YES");
                                        columnChecks.put(column, true);
                                    }
                                    permissionMap.put(PermissionType.UPDATE, new DetailedActionPermission(true, wgo, columnChecks.values().stream().toList()));
                                }
                            }
                        }
                    }

                    if (!permissionMap.containsKey(PermissionType.SELECT))
                    {
                        String viewName = String.format("%s_SELECT_ON_%s", user, table);
                        try (PreparedStatement insertStatement = conn.prepareStatement("SELECT GRANTABLE FROM DBA_TAB_PRIVS WHERE GRANTEE = ? AND OWNER = ? AND TABLE_NAME = ? AND PRIVILEGE = ?"))
                        {
                            insertStatement.setString(1, user);
                            insertStatement.setString(2, schema);
                            insertStatement.setString(3, viewName);
                            insertStatement.setString(4, "SELECT");
                            Map<String, Boolean> columnChecks = new HashMap<>(columns.size());
                            for (String col : columns)
                                columnChecks.put(col, false);
                            try (ResultSet resultSet = insertStatement.executeQuery()) {
                                if (resultSet.next()) {
                                    boolean wgo = resultSet.getString(1).equals("YES");
                                    try (PreparedStatement getSelectColumnsStatement = conn.prepareStatement("SELECT COLUMN_NAME FROM DBA_TAB_COLUMNS WHERE OWNER = ? AND TABLE_NAME = ?")) {
                                        getSelectColumnsStatement.setString(1, schema);
                                        getSelectColumnsStatement.setString(2, viewName);
                                        try (ResultSet selectColumnsSet = getSelectColumnsStatement.executeQuery()) {
                                            while (selectColumnsSet.next())
                                                columnChecks.put(selectColumnsSet.getString(1), true);
                                        }
                                    }
                                    permissionMap.put(PermissionType.SELECT, new DetailedActionPermission(true, wgo, columnChecks.values().stream().toList()));
                                }
                                else {
                                    permissionMap.put(PermissionType.SELECT, new DetailedActionPermission(false, false, columnChecks.values().stream().toList()));
                                }
                            }
                        }
                    }
                }

                return new GeneralPermission(columns, permissionMap);
            }
        } catch (SQLException s) {
            return null;
        }
    }


    @Override
    public List<String> getSchemas() {
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 CallableStatement statement = conn.prepareCall("SELECT GRANTEE FROM DBA_SYS_PRIVS WHERE PRIVILEGE = ? ORDER BY GRANTEE"))
            {
                statement.setString(1, "CREATE TABLE");
                List<String> result = new ArrayList<>();
                try (ResultSet resultSet = statement.executeQuery())
                {
                    while (resultSet.next())
                        result.add(resultSet.getString(1));
                }
                return result;
            }
        } catch (SQLException s) {
            return null;
        }
    }
    private String getGrantSQL(String obj, String user, String action, boolean wgo)
    {
        return String.format("GRANT %s ON %s TO %s %s", action, obj, user, wgo ? "WITH GRANT OPTION" : "");
    }

    private String getRevokeSQL(String obj, String user, String action)
    {
        return String.format("{CALL SYS.REVOKE_PRIVILEGE_WITH_CHECK('%s', '%s', '%s')}", action, obj, user);
    }

    private String getSelectSQl(String schema, String table, String user, boolean wgo, List<String> properties)
    {
        StringBuilder cols = new StringBuilder();
        for (String col : properties)
            cols.append(col).append(',');
        cols.deleteCharAt(cols.length() - 1);
        return String.format("{CALL SYS.CREATE_VIEW_FOR_SELECT_PRIVILEGE('%s', '%s', '%s', '%s', '%s')}", user, schema, table, cols, wgo ? " WITH GRANT OPTION" : "");
    }

    private String getUpdateSQlPriv(String obj, String user, boolean wgo, List<String> properties)
    {
        StringBuilder cols = new StringBuilder("(");
        for (String col : properties)
            cols.append(col).append(',');
        cols.setCharAt(cols.length() - 1, ')');
        return String.format("BEGIN SYS.REVOKE_PRIVILEGE_WITH_CHECK('%s', '%s', '%s'); EXECUTE IMMEDIATE('GRANT UPDATE ' || '%s' || ' ON ' || '%s' || ' TO ' || '%s %s'); END;", "UPDATE", obj, user, cols, obj, user, wgo ? "WITH GRANT OPTION" : "");
    }

    private String getRevokeSelectSQL(String schema, String table, String user)
    {
        return String.format("{CALL SYS.REVOKE_SELECT_PRIVILEGE('%s', '%s', '%s')}", user, schema, table);
    }

    @Override
    public boolean modifyPrivileges(String schema, String table, String user, GeneralPermission permission) {
        Map<PermissionType, ActionPermission> permissionMap = permission.permissions();
        ActionPermission insertPermission = permissionMap.get(PermissionType.INSERT);
        ActionPermission deletePermission = permissionMap.get(PermissionType.DELETE);
        ActionPermission selectPermission = permissionMap.get(PermissionType.SELECT);
        ActionPermission updatePermission = permissionMap.get(PermissionType.UPDATE);
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password))
            {
                String objName = schema + '.' + table;
                if (insertPermission != null)
                {
                    String sql = insertPermission.isGranted() ? getGrantSQL(objName, user, "INSERT", insertPermission.isWgo()) : getRevokeSQL(objName, user, "INSERT");
                    try (CallableStatement statement = conn.prepareCall(sql))
                    {
                        statement.execute();
                    }
                }

                if (deletePermission != null)
                {
                    String sql = deletePermission.isGranted() ? getGrantSQL(objName, user, "DELETE", deletePermission.isWgo()) : getRevokeSQL(objName, user, "DELETE");
                    try (CallableStatement statement = conn.prepareCall(sql))
                    {
                        statement.execute();
                    }
                }

                if (updatePermission != null)
                {
                    String sql;
                    List<String> updatePros = new ArrayList<>();
                    List<Boolean> columnChecks = ((DetailedActionPermission)updatePermission).getColumns();
                    for (int i = 0; i < columnChecks.size(); i++)
                        if (columnChecks.get(i))
                            updatePros.add(permission.properties().get(i));
                    if (updatePermission.isGranted())
                    {
                        if (updatePros.size() == permission.properties().size())
                            sql = getGrantSQL(objName, user, "UPDATE", updatePermission.isWgo());
                        else
                            sql = getUpdateSQlPriv(objName, user, updatePermission.isWgo(), updatePros);
                    }
                    else
                        sql = getRevokeSQL(objName, user, "UPDATE");

                    try (CallableStatement statement = conn.prepareCall(sql))
                    {
                        statement.execute();
                    }
                }

                if (selectPermission != null)
                {
                    String sql;
                    List<String> selectPros = new ArrayList<>();
                    List<Boolean> columnChecks = ((DetailedActionPermission)selectPermission).getColumns();
                    for (int i = 0; i < columnChecks.size(); i++)
                        if (columnChecks.get(i))
                            selectPros.add(permission.properties().get(i));
                    if (selectPermission.isGranted())
                    {
                        if (selectPros.size() == permission.properties().size())
                            sql = getGrantSQL(objName, user, "INSERT", selectPermission.isWgo());
                        else
                            sql = getSelectSQl(schema, table, user, selectPermission.isWgo(), selectPros);
                    }
                    else
                        sql = getRevokeSelectSQL(schema, table, user);

                    try (CallableStatement statement = conn.prepareCall(sql))
                    {
                        statement.execute();
                    }
                }

                return true;
            }
        } catch (SQLException s) {
            s.printStackTrace();
            return false;
        }
    }
}
