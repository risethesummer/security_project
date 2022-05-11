package administrator.dao.table.property;

import administrator.dao.DBObject;
import administrator.dao.DBObjectType;

/**
 * administrator.dao
 * Created by NhatLinh - 19127652
 * Date 3/27/2022 - 12:30 AM
 * Description: ...
 */
public abstract class Property extends DBObject {

    protected final String type;
    protected boolean pk;
    protected boolean unique;
    protected boolean fk;
    protected final boolean nullable;

    public void setReferences(References references) {
        this.references = references;
    }

    protected References references;

    public Property(String name, String type, boolean pk, boolean unique,
                    boolean fk, boolean nullable, References references)
    {
        super(name, DBObjectType.PROPERTY);
        this.type = type;
        this.pk = pk;
        this.unique = unique;
        this.fk = fk;
        this.nullable = nullable;
        this.references = references;
    }

    public Property(String name, String type,
                    boolean pk, boolean unique, boolean nullable)
    {
        this(name, type, pk, unique, false, nullable, null);
    }

    public abstract String getColumnTypeSQL();

    public String getNullableSQL()
    {
        if (!nullable && !pk)
            return "NOT NULL";
        return "";
    }

    public String getFKSQL()
    {
        if (fk)
            return String.format(", FOREIGN KEY (%s) REFERENCES %s", name, references.toString());
        return "";
    }

    public String getUnique()
    {
        return unique ? "UNIQUE" : "";
    }

    /**
     * Generate sql statement
     * @return sql statement
     */
    public String getCreateSQL()
    {
        return String.format("%s %s %s %s %s", name, getColumnTypeSQL(), getUnique(), getNullableSQL(), getFKSQL());
    }

    public String getColumnType() {
        return type;
    }

    public boolean isPk() {
        return pk;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isFk() {
        return fk;
    }

    public boolean isNullable() {
        return nullable;
    }

    public References getReferences() {
        return references;
    }

    public void setUnique(boolean b) {
        this.unique = b;
    }

    public void setPK(boolean pk)
    {
        this.pk = pk;
    }

    public void setFK(boolean b) {
        this.fk = b;
    }
}
