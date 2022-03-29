package administrator.dao.table.property;

/**
 * administrator.dao.table.property
 * Created by NhatLinh - 19127652
 * Date 3/27/2022 - 12:00 PM
 * Description: ...
 */
public class NumberProperty extends SizedProperty {

    public int getPrecision() {
        return precision;
    }

    private final int precision;

    public NumberProperty(String name, String type, int length, int precision, boolean pk, boolean unique, boolean fk, boolean nullable, References references) {
        super(name, type, length, pk, unique, fk, nullable, references);
        this.precision = precision;
    }

    public NumberProperty(String name, String type, int length, int precision, boolean pk, boolean unique, boolean nullable) {
        super(name, type, length, pk, unique, nullable);
        this.precision = precision;
    }

    @Override
    public String toString()
    {
        return String.format("%s(%s,%s)", type.toString(), length, precision);
    }
}
