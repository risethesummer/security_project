package administrator.dao.table.property;

/**
 * administrator.dbHandler.table
 * Created by NhatLinh - 19127652
 * Date 3/27/2022 - 11:51 AM
 * Description: ...
 */
public class PropertyFactory {

    public static Property generateProperty(String name, String type, int length, int precision,
                                            boolean pk, boolean unique, boolean nullable, References references)
    {
        Property result;
        switch (type) {
            case ColumnType.CHAR, ColumnType.VARCHAR2, ColumnType.NCHAR, ColumnType.NVARCHAR2, ColumnType.RAW -> result = new SizedProperty(name, type, length, pk, unique, true, nullable, references);
            case ColumnType.DATE -> result = new NoneSizeProperty(name, type, pk, unique, true, nullable, references);
            default -> result = new NumberProperty(name, type, length, precision, pk, unique, true, nullable, references);
        }
        return result;
    }

    public static Property generateProperty(String name, String type, int length, int precision,
                                            boolean pk, boolean unique, boolean nullable)
    {
        Property result;
        switch (type) {
            case ColumnType.CHAR, ColumnType.VARCHAR2, ColumnType.NCHAR, ColumnType.NVARCHAR2, ColumnType.RAW -> result = new SizedProperty(name, type, length, pk, unique, nullable);
            case ColumnType.DATE -> result = new NoneSizeProperty(name, type, pk, unique, nullable);
            default -> result = new NumberProperty(name, type, length, precision, pk, unique, nullable);
        }
        return result;
    }
}
