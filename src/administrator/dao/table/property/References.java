package administrator.dao.table.property;

import java.util.ArrayList;
import java.util.List;

/**
 * administrator.dao
 * Created by NhatLinh - 19127652
 * Date 3/27/2022 - 12:36 AM
 * Description: ...
 */
public record References(String table, String column) {

    public static References fromString(String line)
    {
        try
        {
            String[] parts = line.split("\\(");
            String columnText = parts[1].substring(0, parts[1].length() - 1);
            return new References(parts[0], columnText);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Override
    public String toString()
    {
        if (table.isBlank() || column.isBlank())
            return "";
        return String.format("%s(%s)", table, column);
    }
}
