package administrator.dao.table.property;

/**
 * administrator.dao.table.property
 * Created by NhatLinh - 19127652
 * Date 3/27/2022 - 11:35 AM
 * Description: ...
 */
public class SizedProperty extends Property {

    protected final int length;

    public SizedProperty(String name, String type, int length, boolean pk, boolean unique, boolean fk, boolean nullable, References references)
    {
        super(name, type, pk, unique, fk, nullable, references);
        this.length = length;
    }


    public SizedProperty(String name, String type, int length, boolean pk, boolean unique, boolean nullable)
    {
        super(name, type, pk, unique, nullable);
        this.length = length;
    }

    @Override
    public String getColumnTypeSQL() {
        return String.format("%s(%s)", type.toString(), length);
    }

    public int getLength() {
        return length;
    }

}
