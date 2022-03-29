package administrator.dbHandler.table;

import administrator.dao.DBObject;
import administrator.dao.table.Table;
import administrator.dao.table.property.*;
import administrator.dbHandler.DBAHandler;
import dbHandler.DBHandler;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
                 PreparedStatement statement = conn.prepareStatement("SELECT OWNER, TABLE_NAME, MIN(COLUMN_NAME), MIN(DATA_TYPE) FROM DBA_TAB_COLUMNS GROUP BY OWNER, TABLE_NAME");
                 ResultSet resultSet = statement.executeQuery())
            {
                List<DBObject> result = new ArrayList<>();
                while (resultSet.next())
                    result.add(new Table(resultSet.getString(1), resultSet.getString(2), new ArrayList<>(){
                        {
                            add(new ShownProperty(resultSet.getString(3), resultSet.getString(4)));
                        }
                    }));
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
                 PreparedStatement statement = conn.prepareStatement("SELECT TABLE_NAME FROM DBA_TABLES WHERE TABLE_NAME = ?"))
            {
                statement.setString(1, name);
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
                 PreparedStatement statement = conn.prepareStatement("DROP TABLE " + name + " CASCADE CONSTRAINTS"))
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
                 CallableStatement statement = conn.prepareCall("{CALL GET_TABLE_DETAILS(?, ?, ?)}"))
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
    public List<References> getReferences(String dataType, int length) {
        try {
            try (Connection conn = DBHandler.getInstance().getConnection(userName, password);
                 CallableStatement statement = conn.prepareCall("{CALL GET_SUITABLE_REFFERRENCES(?, ?, ?)}"))
            {
                statement.setString(1, dataType);
                statement.setInt(2, length);
                statement.registerOutParameter(3, OracleTypes.CURSOR);
                List<References> result = new ArrayList<>();
                try (ResultSet resultSet = (ResultSet)statement.getObject(3))
                {
                    String schema, table, column;
                    while (resultSet.next())
                    {
                        schema = resultSet.getString(1);
                        table = resultSet.getString(2);
                        column = resultSet.getString(3);
                        result.add(new References(String.format("%s.%s", schema, table), column));
                    }
                }
                return result;
            }
        } catch (SQLException s) {
            return null;
        }
    }
}
