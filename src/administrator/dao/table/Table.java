package administrator.dao.table;

import administrator.dao.DBObject;
import administrator.dao.DBObjectType;
import administrator.dao.table.property.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * administrator.dao
 * Created by NhatLinh - 19127652
 * Date 3/27/2022 - 12:30 AM
 * Description: ...
 */
public class Table extends DBObject {

    private final List<Property> properties;
    private final String schema;

    public Table(String schema, String name, List<Property> properties) {
        super(name, DBObjectType.TABLE);
        this.properties = properties;
        this.schema = schema;
    }

    @Override
    public String getName()
    {
        return String.format("%s.%s", schema, name);
    }

 /*   public static String getTableNameFromFullName(String fullName)
    {
        return fullName.split("\\.")[1];
    }*/

    public List<Property> getProperties()
    {
        return properties;
    }


    public String getShown()
    {
        if (properties == null || properties.isEmpty())
            return "No property";
        return properties.get(0).getShown();
    }

    public String getCreateSQL() {
        StringBuilder sql = new StringBuilder(String.format("CREATE TABLE %s (", name));
        List<Property> pks = new ArrayList<>();
        for (Property col : properties)
        {
            sql.append(col.getCreateSQL()).append(',');
            if (col.isPk())
                pks.add(col);
        }
        if (!pks.isEmpty())
        {
            sql.append("PRIMARY KEY (");
            for (Property p : pks)
            {
                sql.append(p.getName()).append(',');
            }
            sql.setCharAt(sql.length() - 1, ')'); //Remove the last ','
            sql.append(')');
        }
        else
            sql.setCharAt(sql.length() - 1, ')'); //Remove the last ','

        return sql.toString();
    }

}
