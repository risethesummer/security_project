package administrator.dao.table.property;

/**
 * administrator.dao.table.property
 * Created by NhatLinh - 19127652
 * Date 3/27/2022 - 11:48 AM
 * Description: ...
 */
public class NoneSizeProperty extends Property {

    public NoneSizeProperty(String name, String type, boolean pk, boolean unique, boolean fk, boolean nullable, References references)
    {
        super(name, type, pk, unique, fk, nullable, references);
    }

    public NoneSizeProperty(String name, String type, boolean pk, boolean unique, boolean nullable)
    {
        super(name, type, pk, unique, nullable);
    }

    @Override
    public String getColumnTypeSQL() {
        return type.toString();
    }

}
